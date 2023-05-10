from locust import FastHttpUser, task, events
import random
from datetime import datetime
from time import time, sleep
from pathlib import Path
from locust.runners import WorkerRunner
import os, sys
import requests
import socket
import uuid
from decimal import Decimal
import decimal

pair = 'BTC-USDT'
min_price = 28000
max_price = 30000
price_std_dev = Decimal(290)
qty_min = Decimal(1)/1000
qty_max = Decimal(1)
qty_std_dev = Decimal(1)/100
cancel_percent = 10
jtl_url = 'http://demo-exchange-jtl-service.demo-exchange.svc.cluster.local'
jtl_token = 'at-5c829081-62d6-442b-8496-a16febb3cf72'
jtl_project = 'Demo Exchange'
jtl_scenario = 'BTC-USDT'
jtl_environment = 'test'
engine_url = 'http://match-engine-btc-usdt-service.demo-exchange.svc.cluster.local:5001/BTC-USDT'

class JtlListener:
    # holds results until processed
    csv_results = []
    results_file = None
    filename = None

    def __init__(
            self,
            env,
            project_name: str,
            scenario_name: str,
            environment: str,
            backend_url: str,
            field_delimiter=",",
            row_delimiter="\n",
            timestamp_format="%Y-%m-%d %H:%M:%S",
            flush_size=100,
    ):
        global jtl_token
        self.env = env
        self.runner = self.env.runner
        # default JMeter field and row delimiters
        self.field_delimiter = field_delimiter
        self.row_delimiter = row_delimiter
        # a timestamp format, others could be added...
        self.timestamp_format = timestamp_format
        # how many records should be held before flushing to disk
        self.flush_size = flush_size
        # results filename format
        self.results_timestamp_format = "%Y_%m_%d_%H_%M_%S"
        self._worker_id = f"{socket.gethostname()}_{os.getpid()}"
        self.is_worker_runner = isinstance(self.env.runner, WorkerRunner)


        self.api_token = jtl_token
        self.project_name = project_name
        self.scenario_name = scenario_name
        self.environment = environment
        self.backend_url = backend_url

        # fields set by default in jmeter
        self.csv_headers = [
            "timeStamp",
            "elapsed",
            "label",
            "responseCode",
            "responseMessage",
            "dataType",
            "success",
            "bytes",
            "sentBytes",
            "grpThreads",
            "allThreads",
            "Latency",
            "IdleTime",
            "Connect",
            "Hostname",
            "failureMessage"
        ]
        self.user_count = 0
        events = self.env.events
        events.request.add_listener(self._request)
        if self.is_worker():
            events.report_to_master.add_listener(self._report_to_master)
        else:
            events.test_start.add_listener(self._test_start)
            events.test_stop.add_listener(self._test_stop)
            events.worker_report.add_listener(self._worker_report)

    def _test_start(self, *a, **kw):
        self._create_results_log()

    def _report_to_master(self, client_id, data):
        data['csv'] = self.csv_results
        self.csv_results = []

    def _worker_report(self, client_id, data):
        self.csv_results += data["csv"]
        if len(self.csv_results) >= self.flush_size:
            self._flush_to_log()

    def _create_results_log(self):
        self.filename = "results_" + \
                        datetime.fromtimestamp(time()).strftime(
                            self.results_timestamp_format) + ".csv"
        Path("logs/").mkdir(parents=True, exist_ok=True)
        results_file = open('logs/' + self.filename, "w")
        results_file.write(self.field_delimiter.join(
            self.csv_headers) + self.row_delimiter)
        results_file.flush()
        self.results_file = results_file

    def _flush_to_log(self):
        if self.results_file is None:
            return
        self.results_file.write(self.row_delimiter.join(
            self.csv_results) + self.row_delimiter)
        self.results_file.flush()
        self.csv_results = []

    def _test_stop(self, *a, environment):
        # wait for last reports to arrive
        sleep(5)
        # final writing a data and clearing self.csv_results between restarts
        if not self.is_worker():
            self._flush_to_log()
        if self.results_file:
            self.results_file.write(self.row_delimiter.join(
                self.csv_results) + self.row_delimiter)
        if self.project_name and self.scenario_name and self.api_token and self.environment:
            try:
                self._upload_file()
            except Exception as e:
                print(e)

    def _upload_file(self):
        files = dict(
            kpi=open('logs/' + self.filename, 'rb'),
            environment=(None, self.environment),
            status=(None, 1))
        url = '%s:5000/api/projects/%s/scenarios/%s/items' % (
            self.backend_url, self.project_name, self.scenario_name)
        response = requests.post(url, files=files, headers={
            'x-access-token': self.api_token})
        if response.status_code != 200:
            raise Exception("Upload failed: %s" % response.text)

    def add_result(self, _request_type, name, response_time, response_length, response, context, exception, **kw):
        timestamp = str(int(round(time() * 1000)))
        response_message = str(response.reason) if "reason" in dir(response) else ""
        status_code = response.status_code
        success = "false" if exception else "true"
        # check to see if the additional fields have been populated. If not, set to a default value
        data_type = kw["data_type"] if "data_type" in kw else "unknown"
        bytes_sent = kw["bytes_sent"] if "bytes_sent" in kw else "0"
        group_threads = str(self.runner.user_count)
        all_threads = str(self.runner.user_count)
        latency = kw["latency"] if "latency" in kw else "0"
        idle_time = kw["idle_time"] if "idle_time" in kw else "0"
        connect = kw["connect"] if "connect" in kw else "0"
        hostname = self._worker_id

        row = [
            timestamp,
            str(round(response_time)),
            name,
            str(status_code),
            response_message,
            data_type,
            success,
            str(response_length),
            bytes_sent,
            str(group_threads),
            str(all_threads),
            latency,
            idle_time,
            connect,
            hostname,
            str(exception)
        ]
        # Safe way to generate csv row up to RFC4180
        # https://datatracker.ietf.org/doc/html/rfc4180
        # It encloses all fields in double quotes and escape single double-quotes chars with double double quotes.
        # Example: " -> ""
        csv_row_str = self.field_delimiter.join(['"' + x.replace('"', '""') + '"' for x in row])
        self.csv_results.append(csv_row_str)
        if len(self.csv_results) >= self.flush_size and not self.is_worker():
            self._flush_to_log()

    def _request(self, request_type, name, response_time, response_length, response, context, exception, **kw):
        self.add_result(request_type, name,
                        response_time, response_length, response, context, exception)

    def is_worker(self):
        return "--worker" in sys.argv

    
def random_number_between(a: Decimal, b: Decimal, std_dev_percentage: Decimal = Decimal(5)/10):
    mean = (b + a) / 2
    std_dev = std_dev_percentage
    random_number = Decimal(random.gauss(float(mean), float(std_dev)))
    return Decimal('{0:.4f}'.format(min(max(random_number, a), b)))

def order_generator(min_price: Decimal, max_price: Decimal, qty_min: Decimal, qty_max: Decimal):
    base_tick = 10**18
    quote_tick = 10**18
    SIDE_BUY = 'BUY'
    SIDE_SELL = 'SELL'
    sides = [SIDE_SELL, SIDE_BUY]
    while True:
        side = random.choice(sides)
        user_id = str(uuid.uuid4())
        order_id = str(uuid.uuid4())
        price = Decimal(random_number_between(min_price, max_price, price_std_dev))
        qty = Decimal(random_number_between(qty_min, qty_max, qty_std_dev))
        total = price * qty
        print(f'{price} {qty} {total}')
        req_price = '{0:.0f}'.format(Decimal(price)*quote_tick)
        req_qty = '{0:.0f}'.format(Decimal(qty)*base_tick)
        req_total = '{0:.0f}'.format(Decimal(total) * quote_tick)
        yield {
            'id': order_id,
            "uid": user_id,
            "side": side,
            "price": req_price,
            "qty": req_qty,
            "total": req_total,
            'utc': int(datetime.utcnow().timestamp() * 1000)
        }

orders = order_generator(Decimal(min_price), Decimal(max_price), qty_min, qty_max)

class MyUser(FastHttpUser):

    host = engine_url

    @task
    def index(self):
        global orders
        order = next(orders)
        req_price = '{0:.0f}'.format(Decimal(order['price']))
        req_qty = '{0:.0f}'.format(Decimal(order['qty']))
        req_total = '{0:.0f}'.format(Decimal(order['total']))
        body = {
            'id': order['id'],
            "uid": order['uid'],
            "side": order['side'],
            "price": req_price,
            "qty": req_qty,
            "total": req_total,
            'utc': int(datetime.utcnow().timestamp() * 1000)
        }
        self.client.post("/order/limit", json=body)
        cancel_percent = random.randrange(100) <= 10
        if cancel_percent:
            self.client.post("/order/cancel", json={
                'id': order['id'],
                'side': order['side'],
                'price': order['price']
            })

@events.init.add_listener
def on_locust_init(environment, **_kwargs):
    JtlListener(env=environment, environment=jtl_environment,  project_name=jtl_project,
                scenario_name=jtl_scenario,
                backend_url=jtl_url)