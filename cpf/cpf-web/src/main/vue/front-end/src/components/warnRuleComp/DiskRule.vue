<template>
<div>
    <el-form :rules="rules" :model="data.config" ref="ruleForm">
    <el-row>
      <el-col :span="20">
        <el-form-item label="名称" prop="name">
          <el-input v-model="data.config.name" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item label="host名称">
          <el-input v-model="data.config.host" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item label="持续时间" prop="time" >
          <el-input v-model="data.config.time" auto-complete="off" placeholder="单位为分钟，为空代表立即报警"></el-input>
        </el-form-item>
        <el-form-item label="Current_Disk_Queue_Length最大值" prop="Current_Disk_Queue_Length">
            <el-input v-model="data.config.Current_Disk_Queue_Length"  placeholder="正整数，为空条件不限制"></el-input>
        </el-form-item>
        <el-form-item label="Free_Megabytes最小值" prop="Free_Megabytes">
            <el-input v-model="data.config.Free_Megabytes" placeholder="正整数，为空条件不限制"></el-input>
        </el-form-item>    
        <el-form-item label="Percent_Disk_Read_Time最大值" prop="Percent_Disk_Read_Time">
            <el-input v-model="data.config.Percent_Disk_Read_Time"  placeholder="0~100，为空条件不限制"></el-input>
        </el-form-item> 
        <el-form-item label="Percent_Disk_Time最大值" prop="Percent_Disk_Time">
            <el-input v-model="data.config.Percent_Disk_Time"  placeholder="0~100，为空条件不限制"></el-input>
        </el-form-item> 
        <el-form-item label="Percent_Disk_Write_Time最大值" prop="Percent_Disk_Write_Time">
            <el-input v-model="data.config.Percent_Disk_Write_Time"  placeholder="0~100，为空条件不限制"></el-input>
        </el-form-item>
        <el-form-item label="Percent_Free_Space最小值" prop="Percent_Free_Space">
            <el-input v-model="data.config.Percent_Free_Space"  placeholder="0~100，为空条件不限制"></el-input>
        </el-form-item>
        <el-form-item label="Percent_Idle_Time最大值" prop="Percent_Idle_Time">
            <el-input v-model="data.config.Percent_Idle_Time"  placeholder="0~100，为空条件不限制"></el-input>
        </el-form-item>                            
      </el-col>
    </el-row>
  </el-form>
    <div slot="footer" class="dialog-footer">
    <el-button @click="closeDialog">取 消</el-button>
    <el-button type="primary" @click="submit">确 定</el-button>
  </div>
</div>
</template>
<script>
import { mapState } from "vuex";
import {saveRule} from "@/api/getData";
import {checkPercent,checkInteger} from "@/utils/validate"
export default {
  name: "DiskRule",
  data() {
    return {
      rules: {
        Current_Disk_Queue_Length: [{ validator: checkInteger, trigger: "blur" }],
        Free_Megabytes: [{ validator: checkInteger, trigger: "blur" }],
        Percent_Disk_Read_Time: [{ validator: checkPercent, trigger: "blur" }],
        Percent_Disk_Time: [{ validator: checkPercent, trigger: "blur" }],
        Percent_Disk_Write_Time: [{ validator: checkPercent, trigger: "blur" }],
        Percent_Free_Space: [{ validator: checkPercent, trigger: "blur" }],
        Percent_Idle_Time: [{ validator: checkPercent, trigger: "blur" }],
        time:[{ validator: checkInteger, trigger: "blur" }],
        name:[{required:true,message:'不能为空'}]
      }
    };
  },
  methods: {
     submit() {
      this.$refs.ruleForm.validate((valid)=>{
        if(valid){
          this.saveModel();
        }else{

        }
      });
    },
    closeDialog() {
      this.$store.commit("closeRuleAddDialog");
    },
    async saveModel() {
      this.data.name = this.data.config.name;
      this.data.time = this.data.config.time;
      const response = await saveRule(this.data);
      if(response.success){
        this.$emit("saveRule",response.result);
        this.$message('保存规则成功');
        this.closeDialog();
      }else{
        this.$message('保存规则失败');
      }
    }
  },
  props: {
    data: {}
  },
  created(){
    if(this.data.config == undefined){
      //elementUI 表单校验只能校验单层属性，所以将name放在config层
      this.data.config = {name:this.data.name};
    }
  }
};
</script>
