//cookie名
c_name = "sample_cookie";
//有効期限
c_day = 365; //日数
//データの区切り
c_split = "_,_"; //データに同じ文字列が含まれないこと

//cookie読込
//load_cookie();

//cookieの読込
function load_cookie() {
    var c_data,n,m,data;
    //cookieの読込
    c_data = loadCookie(c_name);

    if (c_data != "") {
        //データがある場合
        data = c_data.split(c_split);
        //テキストフィールド
        document.report.detail.value = data[0];
        //セレクトメニュー
        document.report.type[data[1]].selected = true;
        Select2Decision(data[1]);   // 追加
        document.report.category[data[2]].selected=true;
        //alert("wwwww");
        //チェックボックス
        //document.form1.checkBox.checked = data[2];
        //ラジオボタン
        //document.form1.radioButton[data[3]].checked = true;
    }
}

//cookieの保存
function save_cookie() {
    var n,c_data;
    //保存データの準備
    //テキストフィールド
    c_data = document.report.detail.value + c_split;
    //セレクトメニュー
    c_data += document.report.type.selectedIndex + c_split;
    c_data += document.report.category.selectedIndex + c_split;
    //alert("asasa");
    //チェックボックス
    //c_data += document.form1.checkBox.checked + c_split;
    //ラジオボタン
    // for (n=0;n<document.form1.radioButton.length;n++) {
    //     if (document.form1.radioButton[n].checked) {
    //         c_data += n + c_split;
    //     }
    // }
    //cookieの保存
    saveCookie(c_name,c_data,c_day);
}

//cookie読込
function loadCookie(c_name) {
    var s,n,m,c_data;
    //cookieの読み込み
    c_data = document.cookie;
    //cookie名
    c_name = c_name + "=";
    //有効なcookie名を調べる
    n = c_data.indexOf(c_name,0);
    if (n > -1) {
        //cookieのデータ部分を取り出す
        m = c_data.indexOf(";",n + c_name.length);
        if (m == -1) m = c_data.length;
        s = c_data.substring(n + c_name.length,m);
        //デコード
        return unescape(s);
    } else {
        return "";
    }
}

//cookie保存
function saveCookie(c_name,c_data,c_day) {
    var n,c_date,c_limit;
    //有効期限
    c_date = new Date();
    n = c_date.getTime() + (1000*60*60*24*c_day);
    c_date.setTime(n);
    c_limit = c_date.toGMTString();
    //cookieの書き出し
    document.cookie = c_name + "=" + escape(c_data) + "; expires=" + c_limit;
}

// 削除するときに呼ばれるメソッド
function deleteCookie() {
    //alert("delete");
    document.cookie = "sample_cookie=; expires=Thu, 01 Jan 1970 00:00:00 GMT";
}

function getcookie() {
    alert(document.cookie);
}

// 値自体をNULLにして戻す方法
// * Cookieのname属性がそもそも一つしか無いため
function deleteCookie2() {
    console.log("delete");

    // 値を空文字にしてしまう
    var c_data = load_cookie(c_name);
    //データがある場合
    var data = c_data.split(c_split);
    data[0] = "";
    data[1] = "";
    data[2] = "";
}

//Cookieのselect2はselect1の選択肢に関わらず「選んだ番号」を選択している
//のでchangeselectでも舗装除雪にかかわらず選択されてしまう　例　除雪　道路が爆発してしまう
//なのでとりあえずcahangeselectに引数でどれのselect1を選んだか渡した
//
function Select2Decision(select1) {
    //select用で
    var select2;
    if(select1==1){
        select2=="舗装";
    }else if(select1==2){
        select2="除雪";
    }else if(select1==3){
        select2="その他";
    }
    changeSelect(select2);
}