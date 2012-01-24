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
def exchangeName = 'logs'
def queueName = channel.queueDeclare().getQueue() // Each suscriber has its own random queue
channel.exchangeDeclare(exchangeName, "fanout")
channel.queueBind(queueName, exchangeName, '')

// Consume
QueueingConsumer consumer = new QueueingConsumer(channel)
channel.basicConsume(queueName, true, consumer)

while(true) {
	QueueingConsumer.Delivery delivery = consumer.nextDelivery()
	def msg = new String(delivery.body)
	println msg
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
