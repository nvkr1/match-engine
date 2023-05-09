
import random
import uuid
from decimal import Decimal
import datetime

pair = 'BTC-USDT'
jtl_url = 'http://demo-exchange-jtl-service'
jtl_token = 'at-5c829081-62d6-442b-8496-a16febb3cf72'
jtl_project = 'Demo Exchange'
jtl_scenario = 'Bullish Scenario'
jtl_environment = 'test'
engine_url = 'http://match-engine-btc-usdt-service/BTC-USDT'

def random_number_between(a: Decimal, b: Decimal, std_dev_percentage: Decimal = Decimal(1)):
    mean = (b + a) / 2
    std_dev = std_dev_percentage
    random_number = Decimal(random.gauss(float(mean), float(std_dev)))
    return min(max(random_number, a), b)

def order_generator(start_price: Decimal, qty_min: Decimal, qty_max: Decimal):
    qty = Decimal(random_number_between(qty_min, qty_max))
    total = start_price * qty
    price = start_price
    base_tick = 10**18
    quote_tick = 10**18
    SIDE_BUY = 'BUY'
    SIDE_SELL = 'SELL'
    side_idx = 0
    sides = [SIDE_SELL, SIDE_BUY]
    side = sides[side_idx % len(sides)]
    while True:
        user_id = str(uuid.uuid4())
        order_id = str(uuid.uuid4())
        total = price * qty
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
            'utc': int(datetime.datetime.utcnow().timestamp() * 1000)
        }
        side_idx += 1
        price += 1
        side = sides[side_idx % len(sides)]

orders = order_generator(Decimal(25000), Decimal(1), Decimal(1))

print(next(orders))
print(next(orders))
print(next(orders))