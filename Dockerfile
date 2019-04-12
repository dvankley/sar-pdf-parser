FROM ubuntu

RUN apt-get update && \
  apt-get install -y tesseract-ocr libtesseract-dev imagemagick

COPY convert-to-pdf /usr/bin/convert-to-pdf
RUN chmod +x /usr/bin/convert-to-pdf

CMD ["/usr/bin/convert-to-pdf", "/images"]
