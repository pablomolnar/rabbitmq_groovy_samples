import com.rabbitmq.client.*

// Annotate the import is not working :S
@Grab(group='com.rabbitmq', module='amqp-client', version='2.5.1')
class Dummy {}

try{

// Connect
def connectionFactory = new ConnectionFactory([username: 'guest', password: 'guest', virtualHost: '/'])
def connection = connectionFactory.newConnection(['127.0.0.1', 5672] as Address)
def channel = connection.createChannel()

// Declare
def queueName = channel.queueDeclare().getQueue()
channel.queueBind(queueName, 'amq.headers', '', [animal: 'rabbit', color: 'green', 'x-match': 'any'])

// Consume
QueueingConsumer consumer = new QueueingConsumer(channel)
channel.basicConsume(queueName, true, consumer)

while(true) {
	QueueingConsumer.Delivery delivery = consumer.nextDelivery()
	def msg = new String(delivery.body)
	println msg
}

} 
catch(e){ e.printStackTrace() }
finally {
  channel.close()
  connection.close()
}

