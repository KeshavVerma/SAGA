# SAGA Implementation
# refer https://fullstackdeveloper.guru/2023/05/11/how-to-implement-saga-design-pattern-in-spring-boot/
Let’s consider the example of a ecommerce application.

A customer places an order and the order gets shipped.

This is the business use case.

Let’s say there are four different microservices to take care of this flow.

An order microservice which handles the customer orders.

A payment microservice which handles payments for the orders.

An inventory microservice which updates the inventory once orders are placed.

A shipping microservice which deals with delivering the orders.

![image](https://github.com/KeshavVerma/SAGA/assets/17050463/9ae4dbd3-8504-408c-b143-a514c0ff79d9)

Note that in real case , separating these functionalities into four different apps may not be a good design . If your user base is small you can do all the above in a single monolith instead of four different microservices which is going to increase your network calls and infrastructure cost. Also handling transactions in a monolith is way more easier.

For this example though , we will go with this design to understand SAGA design pattern.

Now let’s consider the below functions in each microservice when a customer places an order:

createOrder() – Oder microservice
processPayment() – Payment microservice
updateInventory() – Inventory microservice
shipOrder() – Shipping Microservice
When a customer places an order and createOrder() , processPayment() methods succeed and updateInventory() method fails then the system will have a wrong inventory information. And the customer won’t get her order shipped!

So all these tasks have to be part of a single transaction.

You can use SAGA design pattern to implement distributed transaction.

To resolve the above issue , you can rollback the entire transaction using backward recovery.

You can have a compensation task for each of the tasks above.

Here are the compensating tasks

reverseOrder() – Order microservice
reversePayment() – Payment microservice
reverseInventory() – Inventory microservice
Here is the updated flow:

![image](https://github.com/KeshavVerma/SAGA/assets/17050463/cfcfb97c-7933-4d2e-8d2c-e3617c23348e)


Now if updateInventory() method fails, then you call reversePayment() and then reverseOrder() and the order will be rolled back!
