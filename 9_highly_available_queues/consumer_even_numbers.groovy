import com.rabbitmq.client.*

// Annotate the import is not working :S
@Grab(group='com.rabbitmq', module='amqp-client', version='2.5.1')
class Dummy {}

try{

// Get rabbitmq config
def config = new ConfigSlurper().parse(new File('../rabbitmq.properties').toURL())

// Connect
def connectionFactory = new ConnectionFactory([username: config.username, password: config.password, virtualHost: config.virtualHost])
def connection = connectionFactory.newConnection(Address.parseAddresses(config.addresses))
def channel = connection.createChannel()

// Consume
QueueingConsumer consumer = new QueueingConsumer(channel)
channel.basicConsume('evenQueue', true, consumer)

while(true) {
	QueueingConsumer.Delivery delivery = consumer.nextDelivery()
	def msg = new String(delivery.body)
	println msg
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
