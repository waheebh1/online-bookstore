$(document).ready(function () {
    $.ajax({
        url: "http://localhost:8080/books" 
        /** NOTE: this will be an endpoint in a controller that will retrieve books from the repository
        for now, we can have a few books inside the repo just to show **/
    }).then(function (data) {
        if (data) {
            data.books.forEach(function (book) {
                $('.books-info').append(
                    "<div>Name: " + book.name +
                    ", Author: " + book.author + "</div>"
                );
            });
        }
    });
});


$(document).ready(function () {
    $.ajax({
        url: "http://localhost:8080/cart" 
        /** NOTE: this will be an endpoint in a controller that will retrieve current quantity in cart
        will be updated on webpage as we add/remove to cart **/
    }).then(function (data) {
        if (data) {
            $('.books-quantity').append(data.quantity);
        }
    });
});

