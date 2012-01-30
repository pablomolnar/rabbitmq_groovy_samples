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

def exchangeName = "my-custom-exchange"
def queueName = "test-queue"
def routingKey = ''

// Consume
QueueingConsumer consumer = new QueueingConsumer(channel)
channel.basicConsume(queueName, consumer)

while(true){
	QueueingConsumer.Delivery delivery = consumer.nextDelivery()
  def msg = new String(delivery.body)
	println "Message: $msg"
  
  // Manual ack
	channel.basicAck(delivery.envelope.deliveryTag, false)
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
