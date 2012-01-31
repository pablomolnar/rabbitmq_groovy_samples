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


def exchangeName = '' // Direct exchange
def queueName = 'test-queue'
def routingKey = 'test-queue'


// Declare
channel.queueDeclare(queueName, false, false, false, null)

// Publish
100.times {
	channel.basicPublish(exchangeName, routingKey, null, "Hello world!".bytes)
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
