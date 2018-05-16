<template> 
   <div class="hello"> 
    <el-container> 
     <el-header>
      <el-menu class="el-menu-demo" mode="horizontal" background-color="#324057" text-color="#fff" active-text-color="#ffffff">
        <el-menu-item index="1">
          云平台
        </el-menu-item>
      </el-menu>
     </el-header> 
     <el-container> 
      <el-aside width="200px"> 
       <el-row > 
        <el-col> 
         <el-menu default-active="2" class="el-menu-vertical" background-color="#545c64" text-color="#fff" active-text-color="#ffd04b" > 
            <el-menu-item index="SmartWarn" @click="$router.push('/SmartWarn')">
             报警信息
            </el-menu-item> 
            <el-menu-item index="Monitor" @click="$router.push('/Monitor')">
             性能监控
            </el-menu-item> 
            <el-menu-item index="WarnRule" @click="$router.push('/WarnRule')">
             报警规则
            </el-menu-item>
            <el-menu-item index="AlgorithmConfig" @click="$router.push('/AlgorithmConfig')">
             预警算法
            </el-menu-item>
            <!-- <el-menu-item index="SolutionConfig" @click="$router.push('/SolutionConfig')">
             解决方案
            </el-menu-item> -->
         </el-menu> 
        </el-col> 
      </el-row> 
      </el-aside> 
      <el-main> 
       <router-view></router-view> 
      </el-main> 
     </el-container> 
    </el-container> 
   </div> 
</template> 
<script>
export default {
  name: "Layout",
  data() {
    return {
    };
  }
};
</script> 
  <style scoped="">
.el-header {
  background-color: #324057;
  color: #333;
  width: 100%;
}

.el-aside {
  background-color: #545c64;
  color: #333;
  text-align: center;
  height: 1000px;
}

.el-main {
  background-color: #e9eef3;
  color: #333;
  text-align: center;
  height: 1000px;
}
</style>