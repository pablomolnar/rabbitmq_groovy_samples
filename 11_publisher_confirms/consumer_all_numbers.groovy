import com.rabbitmq.client.*

// Annotate the import is not working :S
@Grab(group='com.rabbitmq', module='amqp-client', version='2.5.1')
class Dummy {}

try{

// Get rabbitmq config
def config = new ConfigSlurper().parse(new File('../rabbitmq.properties').toURL())

// Connect
def rabbit = new RabbitHA(config)
rabbit.onDelivery('allQueue'){ delivery, channel ->
  def msg = new String(delivery.body)
  println msg

  // Manual ack
  channel.basicAck(delivery.envelope.deliveryTag, false)
}

// Close
rabbit.close()

} catch(e){e.printStackTrace()}
