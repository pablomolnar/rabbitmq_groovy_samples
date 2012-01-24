import com.rabbitmq.client.*

// Annotate the import is not working :S
@Grab(group='com.rabbitmq', module='amqp-client', version='2.5.1')
class Dummy {}

try{

// Connect
def connectionFactory = new ConnectionFactory([username: 'guest', password: 'guest', virtualHost: '/'])
def connection = connectionFactory.newConnection(['127.0.0.1', 5672] as Address)
def channel = connection.createChannel()

// Declare entry poing exchange
channel.exchangeDeclare('all_numbers', "fanout")

// Route all messages to amq.headers exchange
channel.exchangeBind('amq.headers', 'all_numbers', '')

//  All numbers durable queue
channel.queueDeclare('allQueue', true, false, false, null)
channel.queueBind('allQueue', 'all_numbers', '')

//  Even numbers durable queue
channel.queueDeclare('evenQueue', true, false, false, null)
channel.queueBind('evenQueue', 'amq.headers', '', ['even': 'true', 'x-match': 'all'])

100.times {
  def headers = [even: (it % 2 == 0) as String]
  def properties = new AMQP.BasicProperties.Builder().headers(headers).build()
	channel.basicPublish('all_numbers', '', properties, "Message $it with headers $headers".getBytes())
  Thread.sleep(100)
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
