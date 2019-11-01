FROM debian:stretch
          
# set timezone to Europ/Berlin
RUN ln -fs /usr/share/zoneinfo/Europe/Berlin /etc/localtime
RUN dpkg-reconfigure -f noninteractive tzdata


RUN apt-get update && apt-get install -y --no-install-recommends \
                wget curl unzip lynx
				
RUN apt-get install -y --no-install-recommends \
				apache2 apache2-utils

RUN apt-get install -y --no-install-recommends \
				libapache2-mod-php7.0 php-common php7.0-xml php-bcmath 
				
RUN a2enmod php7.0


RUN curl -k -O https://immerarchiv.de/Download/Archiv10.PHPRepository.1.4.000.zip
RUN unzip Archiv10.PHPRepository.1.4.000.zip -d /var/www


RUN rm -rf /var/www/html
RUN mv /var/www/Archiv10.PHPRepository.1.4.000 /var/www/html
RUN chown -R www-data /var/www/html
RUN chgrp -R www-data /var/www/html

RUN mkdir /var/BagItRepository
RUN chown -R www-data /var/BagItRepository
RUN chgrp -R www-data /var/BagItRepository

EXPOSE 80
CMD apache2ctl -D FOREGROUND

