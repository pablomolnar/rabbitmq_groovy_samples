import com.rabbitmq.client.*

// Annotate the import is not working :S
@Grab(group='com.rabbitmq', module='amqp-client', version='2.5.1')
class Dummy {}

try{

// Connect
def connectionFactory = new ConnectionFactory([username: 'guest', password: 'guest', virtualHost: '/'])
def connection = connectionFactory.newConnection(['127.0.0.1', 5672] as Address)
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
