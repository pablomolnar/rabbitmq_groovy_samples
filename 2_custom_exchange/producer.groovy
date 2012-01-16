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
def exchangeName = "my-custom-exchange"
def queueName = "test-queue"
def routingKey = ''

channel.exchangeDeclare(exchangeName, "direct", true)
channel.queueDeclare(queueName, true, false, false, null)
channel.queueBind(queueName, exchangeName, routingKey)

// Publish
100.times {
	channel.basicPublish(exchangeName, routingKey, null, "Hello world!".bytes)
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
