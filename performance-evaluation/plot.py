import asyncio
import os
import aio_pika
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
from datetime import datetime
from tortoise import Tortoise

db_url = os.getenv('DB_URL') or 'postgres://demo_exchange:demo_exchange_secret@localhost:5433/demo_exchange'
amqp_url = os.getenv('AMQP_URL') or 'amqp://mq_user:mq_password@localhost:5672/'

async def wait_queue_finish(amqp_url):
    connection = await aio_pika.connect_robust(amqp_url)
    async with connection:
        channel = await connection.channel()
        while True:
            q = 'engine_output.q'
            queue = await channel.declare_queue(q, passive=True)
            message_count = queue.declaration_result.message_count
            if message_count == 0:
                print('queue finished')
                break
            else:
                await asyncio.sleep(1)
                continue
        await connection.close()

async def async_main():
    await Tortoise.init(db_url=db_url, modules={
        'models': []
    })
    print('Waiting for queue to finish')
    await wait_queue_finish(amqp_url)
    
    conn = Tortoise.get_connection('default')

    r, tps_result = await conn.execute_query("""
        select t.tps, t."utc" from (
            select count(eol.engine_output_log_id) as tps, (eol.utc/1000)::int as "utc" from ex_output_log eol group by (eol.utc/1000)::int
            ) as t
            where t.tps > 100
            order by t."utc" asc
    """)
    timestamps = list(map(lambda x: x['utc'], tps_result))
    dates = [datetime.fromtimestamp(ts) for ts in timestamps]
    y_values = list(map(lambda x: x['tps'], tps_result))
    # Create the plot
    fig, ax = plt.subplots()
    ax.set_title('Operation Per Second (7000 RPS)')
    ax.set_ylabel('Number of Operations')
    ax.set_xlabel('Timestamp')
    ax.plot_date(dates, y_values, linestyle='solid', marker=None)

    # Set the x-axis format
    ax.xaxis.set_major_formatter(mdates.DateFormatter('%H:%M:%S'))

    # Rotate x-axis labels for better readability
    plt.xticks(rotation=45)

    _, order_types_result = await conn.execute_query("""
        select count(*), 'OPEN' as "status" from ex_order eo where eo.status = 'OPEN'
        union all
        select count(*), 'CANCELLED' as "status" from ex_order eo where eo.status = 'CANCELLED'
        union all
        select count(*), 'MATCH' as "status" from ex_order eo where eo.status = 'FULFILLED'
        union all
        select count(*), 'PARTIALLY MATCH' as "status" from ex_order eo where eo.status = 'PARTIALLY_FILLED'
    """)
    total = sum(map(lambda r: r['count'] ,order_types_result))

    labels = list(map(lambda t: f"{t['status']}({t['count']})", order_types_result))
    sizes = list(map(lambda t: t['count'], order_types_result))

    fig, ax = plt.subplots()
    ax.set_title(f'Order Types ({total})')
    ax.pie(sizes, labels=labels)

    plt.show()

if __name__ == '__main__':
    asyncio.run(async_main())