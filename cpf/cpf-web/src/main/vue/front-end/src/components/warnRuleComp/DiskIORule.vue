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
        <el-form-item label="Current_Disk_Queue_Length最大值" prop="Current_Disk_Queue_Length">
            <el-input v-model="data.config.Current_Disk_Queue_Length"  placeholder="正整数，为空条件不限制"></el-input>
        </el-form-item>
        <el-form-item label="Disk_Read_Bytes_persec最大值" prop="Disk_Read_Bytes_persec">
            <el-input v-model="data.config.Disk_Read_Bytes_persec" placeholder="正浮点数，为空条件不限制"></el-input>
        </el-form-item>    
        <el-form-item label="Disk_Reads_persec最大值" prop="Disk_Reads_persec">
            <el-input v-model="data.config.Disk_Reads_persec"  placeholder="正浮点数，为空条件不限制"></el-input>
        </el-form-item> 
        <el-form-item label="Disk_Write_Bytes_persec最大值" prop="Disk_Write_Bytes_persec">
            <el-input v-model="data.config.Disk_Write_Bytes_persec"  placeholder="正浮点数，为空条件不限制"></el-input>
        </el-form-item> 
        <el-form-item label="Disk_Writes_persec最大值" prop="Disk_Writes_persec">
            <el-input v-model="data.config.Disk_Writes_persec"  placeholder="正浮点数，为空条件不限制"></el-input>
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
import {checkPercent,checkInteger,checkDouble} from "@/utils/validate"
export default {
  name: "DiskIORule",
  data() {
    return {
      rules: {
        Current_Disk_Queue_Length: [{ validator: checkInteger, trigger: "blur" }],
        Disk_Read_Bytes_persec: [{ validator: checkDouble, trigger: "blur" }],
        Disk_Reads_persec: [{ validator: checkDouble, trigger: "blur" }],
        Disk_Write_Bytes_persec: [{ validator: checkDouble, trigger: "blur" }],
        Disk_Writes_persec: [{ validator: checkDouble, trigger: "blur" }],
        Percent_Disk_Read_Time: [{ validator: checkPercent, trigger: "blur" }],
        Percent_Disk_Time: [{ validator: checkPercent, trigger: "blur" }],
        Percent_Disk_Write_Time: [{ validator: checkPercent, trigger: "blur" }],
        time:[{ validator: checkInteger, trigger: "blur" }],
        name:[{required:true,message:'不能为空', trigger: 'blur'}]
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
