var mymap;  //マップ
var marker; //マーカー
var newLat=0; //最後に送信する緯度
var newLon=0; //最後に送信する経度

function onPageLoad2() {

    //初期設定
    drawMap2();
}


function drawMap2() {

    //地図の表示
    mymap = L.map('mapid');

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        minZoom: 13,
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, '
    }).addTo(mymap);

    function onMapClick(e) {
        //マップをクリックしたときのイベント
        // https://kita-note.com/leaflet-tutorial-4
        //すでにあるMarkerを削除して、新しいMarkerを設置
        mymap.removeLayer(marker);
        marker = L.marker(e.latlng).addTo(mymap).bindPopup(e.latlng.toString()).openPopup();
        //↑の下に以下の二行書けば取得できる　　inputはさむとできない　原因はわからん
        newLat = e.latlng.lat; //緯度取得
        newLon = e.latlng.lng; //経度取得
    }

    var onSuccsess = function onLocationFound(e) {
        //現段階（10月ではまだ住所変換できてないので緯度・経度のまま）
        marker = L.marker(e.latlng).addTo(mymap).bindPopup("現在地\n" + e.latlng).openPopup();
        //↑の下に以下の二行書けば取得できる　　inputはさむとできない　原因はわからん
        newLat = e.latlng.lat; //緯度取得
        newLon = e.latlng.lng; //経度取得
    };

    function onLocationError(e) {
        alert("現在地を取得できませんでした。" + e.message);
    }

    navigator.geolocation.getCurrentPosition(onSuccsess, onLocationError, {enableHighAccuracy: true});

    mymap.on('click', onMapClick);

}


//現在位置の再取得ボタン用関数
function setCurLocation2(){
    if (navigator.geolocation == false){
        alert('現在地を取得できませんでした。');
        return;
    }

    function success(e) {
        mymap.removeLayer(marker);
        var lat  = e.coords.latitude;
        var lng = e.coords.longitude;
        mymap.setView([lat, lng], 15);
        marker = L.marker([lat,lng]).addTo(mymap).bindPopup('現在地\n緯度：' + lat + "\n経度：" + lng).openPopup();
        //追加
        newLat=lat;
        newLon=lng;
    };

    function error() {
        alert('現在地を取得できませんでした。');
    };

    //HightAccを追加
    navigator.geolocation.getCurrentPosition(success, error, {enableHighAccuracy: true});
}