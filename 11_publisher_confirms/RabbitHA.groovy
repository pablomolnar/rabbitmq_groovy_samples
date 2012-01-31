import com.rabbitmq.client.*

/**
 * @author: Pablo Molnar
 * @since: 31/1/12
 *
 * RabbitMQ highly available client.
 * Naive implementation of a basic suscriber/publisher with reconnect logic.
 *
 */
class RabbitHA {
    volatile SortedSet<Long> unconfirmedSet = Collections.synchronizedSortedSet(new TreeSet())
    ConnectionFactory connectionFactory
    Address[] addresses
    Closure init

    Connection connection
    Channel channel
    QueueingConsumer consumer

    public RabbitHA(Map config) {
        this(config, null)
    }

    public RabbitHA(Map config, Closure init){
        this.connectionFactory = new ConnectionFactory([username: config.username, password: config.password, virtualHost: config.virtualHost])
        this.addresses = Address.parseAddresses(config.addresses)
        this.init = init
        connectChannel()
    }

    void onDelivery(String queueName, Closure closure) {
        basicConsume(queueName)
        int i = 0

        while(true) {
            try {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery()
                closure(delivery, channel)
                i = 0
            } catch(e) {
                // Only handle ShutdownSignalException and IOException
		            if(!(e in ShutdownSignalException || e in IOException || e in AlreadyClosedException)) throw e
                e.printStackTrace()
                
                i++
                println "ShutdownSignalException recieved! Reconnection attempt #$i"
                connectChannel()
                basicConsume(queueName)
            }
        }
    }

    void publish(Closure closure) {
        int i = 0
  	    boolean retry = true
        while(retry) {
            try {
                closure(channel)
                i = 0
		            retry = false
            } catch(e) {
                // Only handle ShutdownSignalException and IOException
            		if(!(e in ShutdownSignalException || e in IOException || e in AlreadyClosedException)) throw e

                i++
            		retry = true
                e.printStackTrace()
                println "ShutdownSignalException recieved! Reconnection attempt #$i"
                connectChannel()
            }
        }
    }


    void connectChannel() {
        connection = connectionFactory.newConnection(addresses)
        channel = connection.createChannel()

        println "Succesfully connected to $connection.address"

        if(init) {
            init(channel)
        }
        
        channel.confirmSelect()
    }

    void basicConsume(queueName) {
        consumer = new QueueingConsumer(channel)
        channel.basicConsume(queueName, false, consumer)
    }

    void close() {
        channel.waitForConfirmsOrDie();
    	  channel.close()
    	  connection.close()
    }
}
