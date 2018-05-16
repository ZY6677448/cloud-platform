<template>
<div>
    <el-form :rules="rules" :model="data.config" ref="ruleForm">
    <el-row>
      <el-col :span="20">
        <el-form-item label="名称" prop="name" >
          <el-input v-model="data.config.name" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item label="host名称">
          <el-input v-model="data.config.host" auto-complete="off" ></el-input>
        </el-form-item>
        <el-form-item label="持续时间" prop="time" >
          <el-input v-model="data.config.time" auto-complete="off" placeholder="单位为分钟，为空代表立即报警"></el-input>
        </el-form-item>
        <el-form-item label="Percent_DPC_Time最大值" prop="Percent_DPC_Time">
            <el-input v-model="data.config.Percent_DPC_Time"  placeholder="0~100，为空条件不限制"></el-input>
        </el-form-item>
        <el-form-item label="Percent_Idle_Time最大值" prop="Percent_Idle_Time">
            <el-input v-model="data.config.Percent_Idle_Time" placeholder="0~100，为空条件不限制"></el-input>
        </el-form-item>    
        <el-form-item label="Percent_Interrupt_Time最大值" prop="Percent_Interrupt_Time">
            <el-input v-model="data.config.Percent_Interrupt_Time"  placeholder="0~100，为空条件不限制"></el-input>
        </el-form-item> 
        <el-form-item label="Percent_Privileged_Time最大值" prop="Percent_Privileged_Time">
            <el-input v-model="data.config.Percent_Privileged_Time"  placeholder="0~100，为空条件不限制"></el-input>
        </el-form-item> 
        <el-form-item label="Percent_Processor_Time最大值" prop="Percent_Processor_Time">
            <el-input v-model="data.config.Percent_Processor_Time"  placeholder="0~100，为空条件不限制"></el-input>
        </el-form-item>
        <el-form-item label="Percent_User_Time最大值" prop="Percent_User_Time">
            <el-input v-model="data.config.Percent_User_Time"  placeholder="0~100，为空条件不限制"></el-input>
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
  name: "CpuRule",
  data() {
    return {
      rules: {
        Percent_DPC_Time: [{ validator: checkPercent, trigger: "blur" }],
        Percent_Idle_Time: [{ validator: checkPercent, trigger: "blur" }],
        Percent_Interrupt_Time: [{ validator: checkPercent, trigger: "blur" }],
        Percent_Privileged_Time: [{ validator: checkPercent, trigger: "blur" }],
        Percent_Processor_Time: [{ validator: checkPercent, trigger: "blur" }],
        Percent_User_Time: [{ validator: checkPercent, trigger: "blur" }],
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
