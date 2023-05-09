import csv
import datetime
from decimal import Decimal
import requests
import time

pair = 'BTC-USDT'
base_tick = 10**18
quote_tick = 10**18
file_name = 'data/BTC-USDT-10000-MATCH.csv'
engine_url = f'http://localhost:5001/{pair}'

def main():
    with open(file_name, mode='r') as csv_file:
        request_url = f'{engine_url}/order/limit'
        csv_reader = csv.reader(csv_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        start = time.time()
        for i, row in enumerate(csv_reader):
            order_id, user_id, pair, side, price, qty, total = row
            if not order_id:
                continue
            req_price = '{0:.0f}'.format(Decimal(price))
            req_qty = '{0:.0f}'.format(Decimal(qty))
            req_total = '{0:.0f}'.format(Decimal(total))
            resp = requests.post(request_url, json={
                    'id': order_id,
                    "uid": user_id,
                    "side": side,
                    "price": req_price,
                    "qty": req_qty,
                    "total": req_total,
                    'utc': int(datetime.datetime.utcnow().timestamp() * 1000)
                })
            print(resp.status_code)
        print(f'finished in {time.time() - start}')
if __name__ == '__main__':
    main()