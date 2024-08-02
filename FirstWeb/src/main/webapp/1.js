var xhr=new XMLHttpRequest();
function list(){
    xhr.open('GET','/list',true);
    xhr.onreadystatechange=getlist;
    xhr.send();
}
function getlist(){
    if(xhr.status===200&&xhr.readyState===4){
        var res=xhr.responseText;
        var obj=JSON.parse(res);
        var objs=obj.lists;
        var str="";
        for(var i=0;i<objs.length;i++){
            str += "<tr><td>" + objs[i].name + "</td><td>" + objs[i].num + "</td><td><input type='text' id='" + objs[i].name + "' name='" + objs[i].name + "' value='" + objs[i].num + "'></td></tr>";
        }
        document.getElementById("list").innerHTML=str;
    }
    function submitForm(event) {
        event.preventDefault(); // 阻止表单的默认提交行为

        var form = document.querySelector('form');
        var formData = new FormData(form);

        fetch('/shopcar', {
            method: 'POST',
            body: formData
        })
            .then(response => response.text())
            .then(data => {
                console.log('Success:', data);
                alert('购物车提交成功！');
            })
            .catch((error) => {
                console.error('Error:', error);
                alert('购物车提交失败！');
            });
    }


}