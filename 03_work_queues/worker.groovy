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

def queueName = "test-queue"

// Consume
QueueingConsumer consumer = new QueueingConsumer(channel)
boolean autoAck = false
channel.basicConsume(queueName, autoAck, consumer)

while(true){
	QueueingConsumer.Delivery delivery = consumer.nextDelivery()
	
	// Simulate work
	def cost = new String(delivery.body) as Integer
	println cost
	Thread.sleep(cost * 1000)

  // Manual ack
	channel.basicAck(delivery.envelope.deliveryTag, false)
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
