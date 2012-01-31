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


def exchangeName = ''
def queueName = 'test-queue'
def routingKey = 'test-queue'


// Declare
channel.queueDeclare(queueName, false, false, false, null)

// Publish random numbers [1,5] representing payloads
def random = new Random()
100.times {
	def cost = random.nextInt(5) + 1
	channel.basicPublish(exchangeName, routingKey, null, "$cost".getBytes())
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
