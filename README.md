# Blog
Blog application allows to create personal blog and read others articles

# Api documentation: 

https://github.com/NataForova/blog/blob/dev/api.yaml

it will be also available after the application is launched

http://localhost:8080/swagger-ui/index.html

# How to run in Docker

1. Run 
````
 mvn clean install
````

2. Build docker image

````
 docker build -t your-blog .
````

3. Run the image

````
docker run -d -p 8080:8080 --env-file .env  your-blog
````


Make sure that you have your own .env file with next environment variables:

````
DB_URL= # url for postgres
DB_USERNAME= #db username
DB_PASSWORD= #db password
JWT_SECRET_KEY= #jwt secret key 

````



