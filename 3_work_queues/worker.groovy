import com.rabbitmq.client.*

// Annotate the import is not working :S
@Grab(group='com.rabbitmq', module='amqp-client', version='2.5.1')
class Dummy {}

try{

// Connect
def connectionFactory = new ConnectionFactory([username: 'guest', password: 'guest', virtualHost: '/'])
def connection = connectionFactory.newConnection(['127.0.0.1', 5672] as Address)
def channel = connection.createChannel()

def queueName = "test-queue"

// Consume
QueueingConsumer consumer = new QueueingConsumer(channel)
boolean autoAck = false
channel.basicConsume(queueName, autoAck, consumer)

while(true){
	QueueingConsumer.Delivery delivery = consumer.nextDelivery()
	
	// Simulate work
	def cost = new String(delivery.body) as Integer
	println cost
	Thread.sleep(cost * 1000)

  // Manual ack
	channel.basicAck(delivery.envelope.deliveryTag, false)
}

// Close
channel.close()
connection.close()

} catch(e){e.printStackTrace()}
