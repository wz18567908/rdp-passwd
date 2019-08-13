# rdp-passwd

准备工作：

1.需要准备要使用windows remote desktop应用的用户信息（存于文件USER_INFO_FILE），格式如下:

username password

2.需要修改数据配置文件jdbc.properties（crypt/conf），如下:

url中需要修改为安装chessPro的机器ip

jdbc.url=jdbc:mysql://192.168.0.136:3306/chess_pro?useUnicode=true&characterEncoding=UTF-8&useSSL=false

用户名和密码需要修改（加密过的，加密参考3），默认为ctcloud/ctcloud。一般不需要修改！！！（慎用）

jdbc.username=2GEWXMCFjgc=

jdbc.password=2GEWXMCFjgc=

3.数据库密码加密

crypt.vbs "EE" 明文密码

在crypt目录下的debug.log会生成一个********************password is开头的，内容及为密文。



加密远程登录用户密码：

1.解压crypt.zip

2.打开cmd，执行crypt/bin/crypt.vbs

crypt.vbs "DB" "$USER_INFO_FILE"
