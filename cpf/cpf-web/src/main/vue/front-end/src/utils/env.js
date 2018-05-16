/**
 * 配置编译环境和线上环境之间的切换
 * 
 * baseUrl: 域名地址
 * routerMode: 路由模式
 * baseImgPath: 图片存放地址
 * 
 */
let baseUrl = ''; 
let routerMode = 'history';
let baseImgPath;

if (process.env.NODE_ENV == 'development') {
	  baseUrl = 'http://localhost:8000';
    baseImgPath = 'http://localhost:8000';
}else{
	  baseUrl = 'http://119.23.77.132:8080';
    baseImgPath = 'http://cangdu.org:8001/img/';
}

export {
	baseUrl,
	routerMode,
	baseImgPath
}