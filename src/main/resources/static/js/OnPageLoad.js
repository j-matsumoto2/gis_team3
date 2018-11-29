
/*
* liff.htmlの画面読み込み時に起動する
* */
var mymap;  //マップ
var marker; //マーカー
var newLat=0; //最後に送信する緯度
var newLon=0; //最後に送信する経度
var userName=null;
var type = "type";
var category = "category";
var detail = "detail";
var latitude = "lat";
var longitude = "lng";


window.onload = function(e) {

    // ここでCookieを消す？
    if (document.report.flag.value === "true") {
        deleteCookie();
    }

    //初期設定
    drawMap();
    changeSelect();
    load_cookie();

    //LIFF init
    liff.init(function (data) {
        initApp(data);
    });
};

//--------------------------------------------------------------------------------------------------------------------
//地図の制御
function drawMap() {

    //地図の表示
    mymap = L.map('mapid');

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 18,
        minZoom: 13,
        attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, '
    }).addTo(mymap);

        // Control OSM Geocoder
    var option = {
        position: 'topright', // topright, topleft, bottomright, bottomleft
        text: '検索',
        placeholder: '検索条件を入力してください。'
    }
    var osmGeocoder = new L.Control.OSMGeocoder(option);
    mymap.addControl(osmGeocoder);


    function onLocationFound(e) {
        //現段階（10月ではまだ住所変換できてないので緯度・経度のまま）
        marker = L.marker(e.latlng).addTo(mymap).bindPopup("現在地\n" + e.latlng).openPopup();
        //↑の下に以下の二行書けば取得できる　　inputはさむとできない　原因はわからん
        newLat = e.latlng.lat; //緯度取得
        newLon = e.latlng.lng; //経度取得
    }

    function onLocationError(e) {
        //alert("現在地を取得できませんでした。\nブラウザまたは本体の位置情報設定を見直してください。" + e.message);
        alert("現在の位置情報が取得できないため、\n現在位置を\"千歳駅\"に設定します。");
        marker = L.marker([ 42.8281,141.652328]).addTo(mymap).bindPopup("千歳駅").openPopup();
        newLat = 42.8281;
        newLon = 141.652328;
    }

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

    mymap.on('locationfound', onLocationFound);
    mymap.on('locationerror', onLocationError);
    mymap.on('click', onMapClick);

    //Androidで位置情報が取れないやつ回避
    mymap.locate({setView: true, maxZoom: 16, minZoom: 13, timeout: 20000 ,enableHighAccuracy:true});

}


//--------------------------------------------------------------------------------------------------------------------
//選択肢の制御
function changeSelect(flag) {
    var select1 = document.forms.report.type; //変数select1を宣言
    var select2 = document.forms.report.category; //変数select2を宣言

    select2.options.length = 0; // 選択肢の数がそれぞれに異なる場合、これが重要
    //もしcookieから読み込まれた場合　初期のselect1のvalueはnullになっている
    //のでcookieが読み込まれた時のselect1を指定
    if(select1.options[select1.selectedIndex].value == ""){
        if(flag=="舗装"){
            select1.options[select1.selectedIndex].value=="舗装"
        }else if(flag=="照明灯"){
            select1.options[select1.selectedIndex].value=="照明灯"
        }else if(flag=="道路付属物"){
            select1.options[select1.selectedIndex].value=="道路付属物"
        }else if(flag=="雨水・排水"){
            select1.options[select1.selectedIndex].value=="雨水・排水"
        }else if(flag=="小動物の死骸"){
            select1.options[select1.selectedIndex].value=="小動物の市街"
        }else if(flag=="樹木・雑草"){
            select1.options[select1.selectedIndex].value=="樹木・雑草"
        }else if(flag=="除雪") {
            select1.options[select1.selectedIndex].value == "除雪"
        }else if(flag=="その他") {
            select1.options[select1.selectedIndex].value == "その他"
        }else{
            //submitできなくなるのを対処　のちにnullでおくれないようにする
            select1.options[select1.selectedIndex].value==""
        }
    }
    //その後読み込み
    else if (select1.options[select1.selectedIndex].value == "舗装") {
        select2.options[0] = new Option("道路に穴が空いています");
        select2.options[1] = new Option("道路がへこんでいて振動します");
        select2.options[2] = new Option("段差があり走りづらいです");
        select2.options[3] = new Option("歩道がでこぼこしていて歩きづらいです");
        select2.options[4] = new Option("その他");
    }
    else if (select1.options[select1.selectedIndex].value == "除雪") {
        select2.options[0] = new Option("雪山があって見通しが悪い");
        select2.options[1] = new Option("道路が凸凹で走りづらい");
        select2.options[2] = new Option("歩道が歩きづらい");
        select2.options[3] = new Option("砂箱に砂を補充してほしい");
        select2.options[4] = new Option("その他");
    }
    else if(select1.options[select1.selectedIndex].value=="照明灯"){
        select2.options[0] = new Option("照明灯が消えています");
        select2.options[1] = new Option("照明灯に穴が開いています");
        select2.options[2] = new Option("照明灯が錆ついています");
        select2.options[3] = new Option("照明灯が傾いています");
        select2.options[4] = new Option("その他");
    }
    else if(select1.options[select1.selectedIndex].value=="道路付属物"){
        select2.options[0] = new Option("ガードパイプが曲がっています");
        select2.options[1] = new Option("標識が曲がっています");
        select2.options[2] = new Option("標識が傾いています");
        select2.options[3] = new Option("標識が見えづらくなっています");
        select2.options[4] = new Option("縁石が壊れています");
        select2.options[5] = new Option("その他");
    }
    else if(select1.options[select1.selectedIndex].value=="雨水・排水"){
        select2.options[0] = new Option("雨水が排水されません");
        select2.options[1] = new Option("雨水枡の周りが壊れています");
        select2.options[2] = new Option("いつも水が溜まっています");
        select2.options[3] = new Option("マンホールの周りが壊れています");
        select2.options[4] = new Option("その他");
    }
    else if(select1.options[select1.selectedIndex].value=="小動物の死骸"){
        select2.options[0] = new Option("動物が車に轢かれています");
        select2.options[1] = new Option("鳥が死んでいます");
        select2.options[2] = new Option("その他");
    }
    else if(select1.options[select1.selectedIndex].value=="樹木・雑草"){
        select2.options[0] = new Option("雑草が伸びているので確認してください");
        select2.options[1] = new Option("街路樹の枝が伸びているので確認してください");
        select2.options[2] = new Option("樹木が伸びて交差点が見えづらくなっています");
        select2.options[3] = new Option("樹木が枯れています");
        select2.options[4] = new Option("その他");
    }
    else if (select1.options[select1.selectedIndex].value == "その他") {
        select2.options[0] = new Option("その他");
    }
}

//--------------------------------------------------------------------------------------------------------------------
//選択したファイルの名前をポップアップ(送信時：開発用)
function getFilename() {
    var path = document.getElementById('file').value;
    var regex = /\\|\\/;
    var array = path.split(regex);
    var f_name = array[array.length - 1];
    //alert(f_name);
    document.getElementsByName('filename').value = f_name;
    //alert(document.getElementsByName('filename').value);

    //hiddenに緯度と経度を入れる
    document.getElementById('lat').value=newLat;
    document.getElementById('lng').value=newLon;

    //document.report.submit();
}

//--------------------------------------------------------------------------------------------------------------------
//画像ファイルの取得確認ポップアップ（画像選択時：開発用）
function checkFileName() {

    var obj1 = document.getElementById('file');

    obj1.addEventListener('change', function(evt){
        var file = evt.target.files;
        //alert(file[0].name + "を取得しました。");
        document.report.filename.value = file[0].name;
    },false);

}

//-----------------------------------------------この２つはつかいません-----------------------------------------------
//位置情報をHiddenにつっこむ(うまくいかない. Listenerじゃないとダメです)
function inputLocation(latitude,longitude) {

    /*
    if (latitude != oldLat || longitude != oldLon) {
        document.report.location.value = "緯度：" + latitude + ", 経度：" + longitude;
        oldLat = latitude;
        oldLon = longitude;
    }
    */
}

//できない
function inputLocation2(latitude,longitude) {

    marker.addEventListener('change', function (e) {
        //マーカーが変わった？ときのイベント
        document.report.location.value = latitude + " ," + longitude;
    });

}

//--------------------------------------------------------------------------------------------------------------------
//現在位置の再取得ボタン用関数
function setCurLocation(){
    if (navigator.geolocation == false){
        alert('現在地を取得できませんでした。\n位置情報の設定を見直してください。');
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
    }

    function error() {
        alert("現在の位置情報が取得できないため、\n現在位置を\"千歳駅\"に設定します。");
        marker = L.marker([ 42.8281,141.652328]).addTo(mymap).bindPopup("千歳駅").openPopup();
        newLat = 42.8281;
        newLon = 141.652328;
    }

    navigator.geolocation.getCurrentPosition(success, error, {enableHighAccuracy: true});
}

//--------------------------------------------------------------------------------------------------------------------
//地図の表示切り替え
function Display(no){
    if(no == "no1"){
        document.getElementById("mapid").style.display = "block";
        document.getElementById("maphide").style.display = "none";
    }else if(no == "no2"){
        document.getElementById("mapid").style.display = "none";
        document.getElementById("maphide").style.display = "block";
    }
}

//-------------------------------------------------------------------------------------------------------------------
//LINEid
function initApp(data) {
    //alert("init");
    document.getElementById('lineId').value = data.context.userId;
    //alert(document.getElementById('lineId').value);
    liff.getProfile().then(function (profile) {
        userName = profile.displayName;
        //alert(userName);
    }).catch(function () {
        alert('Eroor! getting DisplayName failed');
    });

    document.getElementById("sendmessagebutton").addEventListener('click', function (ev) {
        type = document.report.type.value;
        if (type != "") {
        category = document.report.category.value;
        detail = document.report.detail.value;
        latitude = newLat;
        longitude = newLon;
        sendMessage();
        //typeがnullの時
    }else{
            alert("必須項目を入力してください");
    }
    });

}

//-------------------------------------------------------------------------------------------------------------------
//sendMessage
function sendMessage() {

    var report = "種別：" + type + "\n"
        + "内容：" + category + "\n"
        + "詳細：" + detail + "\n"
        + "緯度 / 経度：" + latitude + " / " + longitude;

    //alert('send click');
    if (navigator.userAgent.indexOf("Line") !== -1) {
        //alert('Agent: LINE');
        //LINEにテキストを送信
        liff.sendMessages([
            {
                type: 'text',
                text: report
            }
        ]).then(function () {
            window.alert("トークに流したゾ");
            liff.closeWindow();
        }).catch(function () {
            window.alert("トークに流せなかった");
        });
    } else {
        console.log("もんだいない");
    }
}

//サムネ表示
// $(function () {
//     $('#upload').click(function () {
//         $('#file').click();
//     });
//
//     function setImage() {
//         for (var i = 0; i < this.files.length; i++) {
//             var file = this.files[i];
//             fr = new FileReader();
//             fr.onload = function (e) {
//                 var img = $('<img>');
//                 img.attr('src', e.target.result);
//                 img.css('height', '160px');
//                 $('#results').append(img);
//             };
//             fr.readAsDataURL(file);
//         }
//     }
//
//     $('#file').on('change', setImage);
// });

//-------------------------------------------------------------------------------------------------------------------
