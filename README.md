# ImmerArchiv.UploadService



## Test with Repository

cd src/test/resources/docker
docker build -t immerarchiv.repository .

docker run -it --rm -p 8881:80 -h test.immerarchiv.com immerarchiv.repository:latest [bash -> apache2ctl start]

http://localhost:8881/