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
def routingKey = ''
channel.exchangeDeclare(exchangeName, "fanout")
channel.exchangeBind('amq.headers', exchangeName, '')

100.times {
  def headers = [even: (it % 2 == 0) as String]
  def properties = new AMQP.BasicProperties.Builder().headers(headers).build()
	channel.basicPublish(exchangeName, routingKey, properties, "Message $it with headers $headers".getBytes())
  Thread.sleep(100)
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
