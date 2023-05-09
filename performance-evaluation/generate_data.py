import uuid
import csv
import datetime
from decimal import Decimal


n = 10000
pair = 'BTC-USDT'
symbol = pair.replace('-', '/')
base_tick = 10**18
quote_tick = 10**18
SIDE_BUY = 'BUY'
SIDE_SELL = 'SELL'
side_idx = 0
sides = [SIDE_SELL, SIDE_BUY]
side = sides[side_idx % len(sides)]

def generate_orders(count: int, start_price: Decimal, qty: Decimal):
    global side_idx, side, sides
    with open(f'data/BTC-USDT-{n}-MATCH.csv', mode='w+') as csv_file:
        csv_writer = csv.writer(csv_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        # csv_writer.writerow(['order_id', 'user_id', 'pair', 'side', 'price', 'qty', 'total'])
        for price in range(int(start_price), int(start_price)+count):
            price = Decimal(price)
            user_id = uuid.uuid4()
            order_id = uuid.uuid4()
            total = price * qty
            req_price = '{0:.0f}'.format(Decimal(price)*quote_tick)
            req_qty = '{0:.0f}'.format(Decimal(qty)*base_tick)
            req_total = '{0:.0f}'.format(Decimal(total) * quote_tick)
            csv_writer.writerow([order_id, user_id, symbol, side, req_price, req_qty, req_total])
            side_idx += 1
            side = sides[side_idx % len(sides)]

generate_orders(n, Decimal(25000), Decimal(1) / 100000)