import com.rabbitmq.client.*

// Annotate the import is not working :S
@Grab(group='com.rabbitmq', module='amqp-client', version='2.5.1')
class Dummy {}

try{

// Add helper method
Collection.metaClass.getRandom = {def i=((Math.random() * 10000) as Integer) % delegate.size(); delegate[i]}

// Get rabbitmq config
def config = new ConfigSlurper().parse(new File('../rabbitmq.properties').toURL())

// Connect
def connectionFactory = new ConnectionFactory([username: config.username, password: config.password, virtualHost: config.virtualHost])
def connection = connectionFactory.newConnection(Address.parseAddresses(config.addresses))
def channel = connection.createChannel()

// Declare
def exchangeName = 'routing'
channel.exchangeDeclare(exchangeName, "topic")


def speed  = ['quick', 'lazy']
def color  = ['red', 'green', 'blue']
def animal = ['rabbit', 'frog', 'cat']

100.times {
  // "<speed>.<colour>.<species>"
  String routingKey = "${speed.getRandom()}.${color.getRandom()}.${animal.getRandom()}"
  println "Sending $routingKey"
	channel.basicPublish(exchangeName, routingKey, null, "Message with routing key $routingKey".getBytes())
  Thread.sleep(100)
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
