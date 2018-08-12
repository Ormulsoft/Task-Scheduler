package parallel;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import java.util.Collections;//####[4]####
import java.util.Comparator;//####[5]####
import org.apache.log4j.Logger;//####[6]####
import grph.properties.NumericalProperty;//####[7]####
import pt.*;//####[8]####
import alg.Algorithm;//####[9]####
import toools.collections.primitive.LucIntSet;//####[10]####
import util.PartialScheduleGrph;//####[11]####
import util.ScheduleGrph;//####[12]####
import java.util.HashSet;//####[13]####
//####[13]####
//-- ParaTask related imports//####[13]####
import pt.runtime.*;//####[13]####
import java.util.concurrent.ExecutionException;//####[13]####
import java.util.concurrent.locks.*;//####[13]####
import java.lang.reflect.*;//####[13]####
import pt.runtime.GuiThread;//####[13]####
import java.util.concurrent.BlockingQueue;//####[13]####
import java.util.ArrayList;//####[13]####
import java.util.List;//####[13]####
//####[13]####
/**
 * A basic cost function that assigns the end time as the cost.
 * 
 * @author Matt
 *
 *///####[19]####
public class CostFunctionParallel {//####[20]####
    static{ParaTask.init();}//####[20]####
    /*  ParaTask helper method to access private/protected slots *///####[20]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[20]####
        if (m.getParameterTypes().length == 0)//####[20]####
            m.invoke(instance);//####[20]####
        else if ((m.getParameterTypes().length == 1))//####[20]####
            m.invoke(instance, arg);//####[20]####
        else //####[20]####
            m.invoke(instance, arg, interResult);//####[20]####
    }//####[20]####
//####[22]####
    ScheduleGrph input;//####[22]####
//####[23]####
    static final Logger log = Logger.getLogger(Algorithm.class);//####[23]####
//####[24]####
    public CostFunctionParallel(ScheduleGrph input) {//####[24]####
        this.input = input;//####[25]####
    }//####[26]####
//####[29]####
    private static volatile Method __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method = null;//####[29]####
    private synchronized static void __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet() {//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            try {//####[29]####
                __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__getFree", new Class[] {//####[29]####
                    ScheduleGrph.class, PartialScheduleGrph.class, int.class//####[29]####
                });//####[29]####
            } catch (Exception e) {//####[29]####
                e.printStackTrace();//####[29]####
            }//####[29]####
        }//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg, int task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg, int task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, PartialScheduleGrph pg, int task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, PartialScheduleGrph pg, int task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setTaskIdArgIndexes(0);//####[29]####
        taskinfo.addDependsOn(inputSaved);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, PartialScheduleGrph pg, int task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, PartialScheduleGrph pg, int task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(0);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, TaskID<PartialScheduleGrph> pg, int task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, TaskID<PartialScheduleGrph> pg, int task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setTaskIdArgIndexes(1);//####[29]####
        taskinfo.addDependsOn(pg);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, int task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, int task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[29]####
        taskinfo.addDependsOn(inputSaved);//####[29]####
        taskinfo.addDependsOn(pg);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, int task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, int task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(0);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(1);//####[29]####
        taskinfo.addDependsOn(pg);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, BlockingQueue<PartialScheduleGrph> pg, int task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, BlockingQueue<PartialScheduleGrph> pg, int task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(1);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, int task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, int task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(1);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(0);//####[29]####
        taskinfo.addDependsOn(inputSaved);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, int task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, int task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(0, 1);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg, TaskID<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg, TaskID<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setTaskIdArgIndexes(2);//####[29]####
        taskinfo.addDependsOn(task);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, PartialScheduleGrph pg, TaskID<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, PartialScheduleGrph pg, TaskID<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[29]####
        taskinfo.addDependsOn(inputSaved);//####[29]####
        taskinfo.addDependsOn(task);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, PartialScheduleGrph pg, TaskID<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, PartialScheduleGrph pg, TaskID<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(0);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(2);//####[29]####
        taskinfo.addDependsOn(task);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, TaskID<PartialScheduleGrph> pg, TaskID<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, TaskID<PartialScheduleGrph> pg, TaskID<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[29]####
        taskinfo.addDependsOn(pg);//####[29]####
        taskinfo.addDependsOn(task);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, TaskID<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, TaskID<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setTaskIdArgIndexes(0, 1, 2);//####[29]####
        taskinfo.addDependsOn(inputSaved);//####[29]####
        taskinfo.addDependsOn(pg);//####[29]####
        taskinfo.addDependsOn(task);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, TaskID<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, TaskID<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(0);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(1, 2);//####[29]####
        taskinfo.addDependsOn(pg);//####[29]####
        taskinfo.addDependsOn(task);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, BlockingQueue<PartialScheduleGrph> pg, TaskID<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, BlockingQueue<PartialScheduleGrph> pg, TaskID<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(1);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(2);//####[29]####
        taskinfo.addDependsOn(task);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, TaskID<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, TaskID<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(1);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(0, 2);//####[29]####
        taskinfo.addDependsOn(inputSaved);//####[29]####
        taskinfo.addDependsOn(task);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, TaskID<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, TaskID<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(0, 1);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(2);//####[29]####
        taskinfo.addDependsOn(task);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg, BlockingQueue<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg, BlockingQueue<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(2);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, PartialScheduleGrph pg, BlockingQueue<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, PartialScheduleGrph pg, BlockingQueue<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(2);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(0);//####[29]####
        taskinfo.addDependsOn(inputSaved);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, PartialScheduleGrph pg, BlockingQueue<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, PartialScheduleGrph pg, BlockingQueue<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(0, 2);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, TaskID<PartialScheduleGrph> pg, BlockingQueue<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, TaskID<PartialScheduleGrph> pg, BlockingQueue<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(2);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(1);//####[29]####
        taskinfo.addDependsOn(pg);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, BlockingQueue<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, BlockingQueue<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(2);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(0, 1);//####[29]####
        taskinfo.addDependsOn(inputSaved);//####[29]####
        taskinfo.addDependsOn(pg);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, BlockingQueue<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, TaskID<PartialScheduleGrph> pg, BlockingQueue<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(0, 2);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(1);//####[29]####
        taskinfo.addDependsOn(pg);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, BlockingQueue<PartialScheduleGrph> pg, BlockingQueue<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(ScheduleGrph inputSaved, BlockingQueue<PartialScheduleGrph> pg, BlockingQueue<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(1, 2);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, BlockingQueue<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(TaskID<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, BlockingQueue<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(1, 2);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setTaskIdArgIndexes(0);//####[29]####
        taskinfo.addDependsOn(inputSaved);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, BlockingQueue<Integer> task) {//####[29]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[29]####
        return getFree(inputSaved, pg, task, new TaskInfo());//####[29]####
    }//####[29]####
    public TaskID<Boolean> getFree(BlockingQueue<ScheduleGrph> inputSaved, BlockingQueue<PartialScheduleGrph> pg, BlockingQueue<Integer> task, TaskInfo taskinfo) {//####[29]####
        // ensure Method variable is set//####[29]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method == null) {//####[29]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_ensureMethodVarSet();//####[29]####
        }//####[29]####
        taskinfo.setQueueArgIndexes(0, 1, 2);//####[29]####
        taskinfo.setIsPipeline(true);//####[29]####
        taskinfo.setParameters(inputSaved, pg, task);//####[29]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_method);//####[29]####
        taskinfo.setInstance(this);//####[29]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[29]####
    }//####[29]####
    public boolean __pt__getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg, int task) {//####[29]####
        long start = System.currentTimeMillis();//####[34]####
        boolean X = true;//####[35]####
        for (int outEdge : inputSaved.getOutEdges(task)) //####[36]####
        {//####[36]####
            int otherVert = inputSaved.getTheOtherVertex(outEdge, task);//####[37]####
            if (!pg.containsVertex(otherVert)) //####[39]####
            {//####[39]####
                boolean add = true;//####[40]####
                for (int e : inputSaved.getInEdges(otherVert)) //####[42]####
                {//####[42]####
                    if (!pg.containsVertex(inputSaved.getTheOtherVertex(e, otherVert))) //####[43]####
                    {//####[43]####
                        add = false;//####[44]####
                        break;//####[45]####
                    }//####[46]####
                }//####[47]####
                if (add) //####[48]####
                {//####[48]####
                    long time = System.currentTimeMillis() - start;//####[49]####
                    long id = Thread.currentThread().getId();//####[50]####
                    log.info(task + "added:'" + X + "\' [" + (time / 1000.0) + " seconds, thread " + id + "]");//####[51]####
                    return true;//####[52]####
                }//####[53]####
            }//####[54]####
        }//####[55]####
        long time = System.currentTimeMillis() - start;//####[56]####
        long id = Thread.currentThread().getId();//####[57]####
        log.info(task + "added:'" + X + "\' [" + (time / 1000.0) + " seconds, thread " + id + "]");//####[58]####
        return false;//####[61]####
    }//####[62]####
//####[62]####
}//####[62]####
