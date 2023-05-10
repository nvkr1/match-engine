import random
import matplotlib
import matplotlib.pyplot as plt
from decimal import Decimal

n = 100000

prices = []

def random_number_between(a: Decimal, b: Decimal, std_dev: Decimal):
    mean = (b + a) / 2
    random_number = Decimal(random.gauss(float(mean), float(std_dev)))
    return Decimal('{0:.0f}'.format(random_number))

price_counts = {}
for i in range(n):
    price = random_number_between(Decimal(28000), Decimal(30000), Decimal(290))
    if price_counts.get(str(price)):
        price_counts[str(price)] += 1
    else:
        price_counts[str(price)] = 1

x_prices_raw = price_counts.keys()
x_prices = list(map(lambda p: Decimal(p), x_prices_raw))
x_prices.sort()
y_prices = []
for x_price in x_prices:
    price_count = price_counts[str(x_price)]
    y_prices.append(price_count)
plt.ticklabel_format(useOffset=False)
plt.plot(x_prices, y_prices)
plt.show()