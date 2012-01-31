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



def rabbit = new RabbitHA(config)
rabbit.init = { channel ->
  // Declare entry poing exchange
  channel.exchangeDeclare('all_numbers', "fanout", true) // Durable exchange

  // Route all messages to amq.headers exchange
  channel.exchangeBind('amq.headers', 'all_numbers', '')

  //  All numbers durable queue
  channel.queueDeclare('allQueue', true, false, false, ["x-ha-policy": "all"]) // Durable exchange and HA policy
  channel.queueBind('allQueue', 'all_numbers', '')

  //  Even numbers durable queue
  channel.queueDeclare('evenQueue', true, false, false, ["x-ha-policy": "all"]) // Durable exchange and HA policy
  channel.queueBind('evenQueue', 'amq.headers', '', ['even': 'true', 'x-match': 'all']) 

}

100.times { idx ->
  rabbit.publish { channel ->
    def headers = [even: (idx % 2 == 0) as String]
    def properties = new AMQP.BasicProperties.Builder().headers(headers).deliveryMode(2).build() // Delivery mode 2: persistent
    def msg = "Message $idx with headers $headers"
    unconfirmedSet.add(channel.getNextPublishSeqNo());	
    channel.basicPublish('all_numbers', '', properties, msg.getBytes())
    println msg
  }
}

// Close
rabbit.close()

} catch(e){e.printStackTrace()}
