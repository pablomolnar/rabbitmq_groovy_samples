import com.rabbitmq.client.*

// Annotate the import is not working :S
@Grab(group='com.rabbitmq', module='amqp-client', version='2.5.1')
class Dummy {}

try{

// Connect
def connectionFactory = new ConnectionFactory([username: 'guest', password: 'guest', virtualHost: '/'])
def connection = connectionFactory.newConnection(['127.0.0.1', 5672] as Address)
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
