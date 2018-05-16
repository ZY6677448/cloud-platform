<template>
    <el-card class="box-card" :body-style="{ padding: '0px'}" align="left">
        <div slot="header" class="clearfix" >
            <h4>{{content.type}}</h4>
        </div>
        <el-row></el-row>
        <el-row v-for="(val, key) in content.data" :key="key">
            <el-col :offset="1">
                <el-tag type="info"> {{key + ":" + val }}</el-tag>
            </el-col>
        </el-row>
    </el-card>
</template>
<script>
export default {
  data() {
    return {
      
    };
  },
  props: {
    content: {}
  },
  created(){
  }
};
</script>
<style scoped>
  .text {
    font-size: 14px;
  }

  .item {
    margin-bottom: 18px;
  }

  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }

  .box-card {
    width: 400px;
    height:600px;
  }
  .el-row {
  margin-bottom: 50px;
}
</style>