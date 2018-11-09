

function getCurrentLocation(id) {

    //GeolocationAPIのgetCurrentPosition関数を使って現在地を取得
    navigator.geolocation.getCurrentPosition(function (currentPosition) {
        var lat = currentPosition.coords.latitude;
        var lon = currentPosition.coords.longitude;

        var id = "table2";

        $('#'+id + " .lat").html("緯度[deg]: " + lat);
        $('#'+id + " .lon").html("経度[deg]: " + lon);

    }, function (error) {
        console.log(error);
        alert('位置情報が取得できません');
    });

}