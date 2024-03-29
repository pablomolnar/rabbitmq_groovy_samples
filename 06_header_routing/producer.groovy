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

def speed  = ['quick', 'lazy']
def color  = ['red', 'green', 'blue']
def animal = ['rabbit', 'frog', 'cat']

100.times {
  Map<String, Object> headers = [speed: speed.getRandom(), color: color.getRandom(), animal: animal.getRandom()]
  def properties = new AMQP.BasicProperties.Builder().headers(headers).build()

  println "Sending headers $headers"
	channel.basicPublish("amq.headers", '', properties, "Message with headers $headers".getBytes())
  Thread.sleep(100)
}

} 
catch(e){ e.printStackTrace() }
finally {
  channel.close()
  connection.close()
}
