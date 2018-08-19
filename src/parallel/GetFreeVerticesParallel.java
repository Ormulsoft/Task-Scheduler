package parallel;//####[1]####
//####[1]####
import java.util.ArrayList;//####[3]####
import java.util.Collections;//####[4]####
import java.util.Comparator;//####[5]####
import org.apache.log4j.Logger;//####[6]####
import grph.properties.NumericalProperty;//####[7]####
import pt.*;//####[8]####
import java.util.Set;//####[9]####
import java.util.PriorityQueue;//####[10]####
import alg.Algorithm;//####[11]####
import toools.collections.primitive.LucIntSet;//####[12]####
import util.PartialScheduleGrph;//####[13]####
import util.ScheduleGrph;//####[14]####
import java.util.HashSet;//####[15]####
import util.ScheduleDotWriter;//####[16]####
//####[16]####
//-- ParaTask related imports//####[16]####
import pt.runtime.*;//####[16]####
import java.util.concurrent.ExecutionException;//####[16]####
import java.util.concurrent.locks.*;//####[16]####
import java.lang.reflect.*;//####[16]####
import pt.runtime.GuiThread;//####[16]####
import java.util.concurrent.BlockingQueue;//####[16]####
import java.util.ArrayList;//####[16]####
import java.util.List;//####[16]####
//####[16]####
/**
 * A basic cost parallel function that assigns the end time as the cost.
 * 
 * @author Nikhil
 *
 *///####[22]####
public class GetFreeVerticesParallel {//####[23]####
    static{ParaTask.init();}//####[23]####
    /*  ParaTask helper method to access private/protected slots *///####[23]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[23]####
        if (m.getParameterTypes().length == 0)//####[23]####
            m.invoke(instance);//####[23]####
        else if ((m.getParameterTypes().length == 1))//####[23]####
            m.invoke(instance, arg);//####[23]####
        else //####[23]####
            m.invoke(instance, arg, interResult);//####[23]####
    }//####[23]####
//####[25]####
    ScheduleGrph input;//####[25]####
//####[26]####
    static final Logger log = Logger.getLogger(Algorithm.class);//####[26]####
//####[27]####
    public GetFreeVerticesParallel(ScheduleGrph input) {//####[27]####
        this.input = input;//####[28]####
    }//####[29]####
//####[30]####
    public GetFreeVerticesParallel() {//####[30]####
    }//####[31]####
//####[35]####
    private static volatile Method __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_HashSetInteger_method = null;//####[35]####
    private synchronized static void __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_HashSetInteger_ensureMethodVarSet() {//####[35]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_HashSetInteger_method == null) {//####[35]####
            try {//####[35]####
                __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_HashSetInteger_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__getFree", new Class[] {//####[35]####
                    ScheduleGrph.class, PartialScheduleGrph.class, int.class, HashSet.class//####[35]####
                });//####[35]####
            } catch (Exception e) {//####[35]####
                e.printStackTrace();//####[35]####
            }//####[35]####
        }//####[35]####
    }//####[35]####
    public TaskID<HashSet<Integer>> getFree(Object inputSaved, Object pg, Object task, Object a) {//####[35]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[35]####
        return getFree(inputSaved, pg, task, a, new TaskInfo());//####[35]####
    }//####[35]####
    public TaskID<HashSet<Integer>> getFree(Object inputSaved, Object pg, Object task, Object a, TaskInfo taskinfo) {//####[35]####
        // ensure Method variable is set//####[35]####
        if (__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_HashSetInteger_method == null) {//####[35]####
            __pt__getFree_ScheduleGrph_PartialScheduleGrph_int_HashSetInteger_ensureMethodVarSet();//####[35]####
        }//####[35]####
        List<Integer> __pt__taskIdIndexList = new ArrayList<Integer>();//####[35]####
        List<Integer> __pt__queueIndexList = new ArrayList<Integer>();//####[35]####
        if (inputSaved instanceof BlockingQueue) {//####[35]####
            __pt__queueIndexList.add(0);//####[35]####
        }//####[35]####
        if (inputSaved instanceof TaskID) {//####[35]####
            taskinfo.addDependsOn((TaskID)inputSaved);//####[35]####
            __pt__taskIdIndexList.add(0);//####[35]####
        }//####[35]####
        if (pg instanceof BlockingQueue) {//####[35]####
            __pt__queueIndexList.add(1);//####[35]####
        }//####[35]####
        if (pg instanceof TaskID) {//####[35]####
            taskinfo.addDependsOn((TaskID)pg);//####[35]####
            __pt__taskIdIndexList.add(1);//####[35]####
        }//####[35]####
        if (task instanceof BlockingQueue) {//####[35]####
            __pt__queueIndexList.add(2);//####[35]####
        }//####[35]####
        if (task instanceof TaskID) {//####[35]####
            taskinfo.addDependsOn((TaskID)task);//####[35]####
            __pt__taskIdIndexList.add(2);//####[35]####
        }//####[35]####
        if (a instanceof BlockingQueue) {//####[35]####
            __pt__queueIndexList.add(3);//####[35]####
        }//####[35]####
        if (a instanceof TaskID) {//####[35]####
            taskinfo.addDependsOn((TaskID)a);//####[35]####
            __pt__taskIdIndexList.add(3);//####[35]####
        }//####[35]####
        int[] __pt__queueIndexArray = new int[__pt__queueIndexList.size()];//####[35]####
        for (int __pt__i = 0; __pt__i < __pt__queueIndexArray.length; __pt__i++) {//####[35]####
            __pt__queueIndexArray[__pt__i] = __pt__queueIndexList.get(__pt__i);//####[35]####
        }//####[35]####
        taskinfo.setQueueArgIndexes(__pt__queueIndexArray);//####[35]####
        if (__pt__queueIndexArray.length > 0) {//####[35]####
            taskinfo.setIsPipeline(true);//####[35]####
        }//####[35]####
        int[] __pt__taskIdIndexArray = new int[__pt__taskIdIndexList.size()];//####[35]####
        for (int __pt__i = 0; __pt__i < __pt__taskIdIndexArray.length; __pt__i++) {//####[35]####
            __pt__taskIdIndexArray[__pt__i] = __pt__taskIdIndexList.get(__pt__i);//####[35]####
        }//####[35]####
        taskinfo.setTaskIdArgIndexes(__pt__taskIdIndexArray);//####[35]####
        taskinfo.setParameters(inputSaved, pg, task, a);//####[35]####
        taskinfo.setMethod(__pt__getFree_ScheduleGrph_PartialScheduleGrph_int_HashSetInteger_method);//####[35]####
        taskinfo.setInstance(this);//####[35]####
        taskinfo.setInteractive(true);//####[35]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[35]####
    }//####[35]####
    public HashSet<Integer> __pt__getFree(ScheduleGrph inputSaved, PartialScheduleGrph pg, int task, HashSet<Integer> a) {//####[35]####
        long start = System.currentTimeMillis();//####[40]####
        boolean add = true;//####[41]####
        for (int outEdge : inputSaved.getOutEdges(task)) //####[42]####
        {//####[42]####
            int otherVert = inputSaved.getTheOtherVertex(outEdge, task);//####[43]####
            if (!pg.containsVertex(otherVert)) //####[45]####
            {//####[45]####
                add = true;//####[46]####
                for (int e : inputSaved.getInEdges(otherVert)) //####[48]####
                {//####[48]####
                    if (!pg.containsVertex(inputSaved.getTheOtherVertex(e, otherVert))) //####[49]####
                    {//####[49]####
                        add = false;//####[50]####
                        break;//####[51]####
                    }//####[52]####
                }//####[53]####
                if (add) //####[54]####
                {//####[54]####
                    long id = Thread.currentThread().getId();//####[55]####
                    a.add(otherVert);//####[56]####
                }//####[57]####
            }//####[58]####
        }//####[59]####
        return a;//####[60]####
    }//####[61]####
//####[61]####
//####[63]####
    private static volatile Method __pt__getCosts_int_PriorityQueuePartialScheduleGrph_int_PartialScheduleGrph_HashSetString_method = null;//####[63]####
    private synchronized static void __pt__getCosts_int_PriorityQueuePartialScheduleGrph_int_PartialScheduleGrph_HashSetString_ensureMethodVarSet() {//####[63]####
        if (__pt__getCosts_int_PriorityQueuePartialScheduleGrph_int_PartialScheduleGrph_HashSetString_method == null) {//####[63]####
            try {//####[63]####
                __pt__getCosts_int_PriorityQueuePartialScheduleGrph_int_PartialScheduleGrph_HashSetString_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__getCosts", new Class[] {//####[63]####
                    int.class, PriorityQueue.class, int.class, PartialScheduleGrph.class, HashSet.class//####[63]####
                });//####[63]####
            } catch (Exception e) {//####[63]####
                e.printStackTrace();//####[63]####
            }//####[63]####
        }//####[63]####
    }//####[63]####
    public TaskID<PriorityQueue<PartialScheduleGrph>> getCosts(Object numProcessors, Object states, Object task, Object s, Object closedStates) {//####[63]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[63]####
        return getCosts(numProcessors, states, task, s, closedStates, new TaskInfo());//####[63]####
    }//####[63]####
    public TaskID<PriorityQueue<PartialScheduleGrph>> getCosts(Object numProcessors, Object states, Object task, Object s, Object closedStates, TaskInfo taskinfo) {//####[63]####
        // ensure Method variable is set//####[63]####
        if (__pt__getCosts_int_PriorityQueuePartialScheduleGrph_int_PartialScheduleGrph_HashSetString_method == null) {//####[63]####
            __pt__getCosts_int_PriorityQueuePartialScheduleGrph_int_PartialScheduleGrph_HashSetString_ensureMethodVarSet();//####[63]####
        }//####[63]####
        List<Integer> __pt__taskIdIndexList = new ArrayList<Integer>();//####[63]####
        List<Integer> __pt__queueIndexList = new ArrayList<Integer>();//####[63]####
        if (numProcessors instanceof BlockingQueue) {//####[63]####
            __pt__queueIndexList.add(0);//####[63]####
        }//####[63]####
        if (numProcessors instanceof TaskID) {//####[63]####
            taskinfo.addDependsOn((TaskID)numProcessors);//####[63]####
            __pt__taskIdIndexList.add(0);//####[63]####
        }//####[63]####
        if (states instanceof BlockingQueue) {//####[63]####
            __pt__queueIndexList.add(1);//####[63]####
        }//####[63]####
        if (states instanceof TaskID) {//####[63]####
            taskinfo.addDependsOn((TaskID)states);//####[63]####
            __pt__taskIdIndexList.add(1);//####[63]####
        }//####[63]####
        if (task instanceof BlockingQueue) {//####[63]####
            __pt__queueIndexList.add(2);//####[63]####
        }//####[63]####
        if (task instanceof TaskID) {//####[63]####
            taskinfo.addDependsOn((TaskID)task);//####[63]####
            __pt__taskIdIndexList.add(2);//####[63]####
        }//####[63]####
        if (s instanceof BlockingQueue) {//####[63]####
            __pt__queueIndexList.add(3);//####[63]####
        }//####[63]####
        if (s instanceof TaskID) {//####[63]####
            taskinfo.addDependsOn((TaskID)s);//####[63]####
            __pt__taskIdIndexList.add(3);//####[63]####
        }//####[63]####
        if (closedStates instanceof BlockingQueue) {//####[63]####
            __pt__queueIndexList.add(4);//####[63]####
        }//####[63]####
        if (closedStates instanceof TaskID) {//####[63]####
            taskinfo.addDependsOn((TaskID)closedStates);//####[63]####
            __pt__taskIdIndexList.add(4);//####[63]####
        }//####[63]####
        int[] __pt__queueIndexArray = new int[__pt__queueIndexList.size()];//####[63]####
        for (int __pt__i = 0; __pt__i < __pt__queueIndexArray.length; __pt__i++) {//####[63]####
            __pt__queueIndexArray[__pt__i] = __pt__queueIndexList.get(__pt__i);//####[63]####
        }//####[63]####
        taskinfo.setQueueArgIndexes(__pt__queueIndexArray);//####[63]####
        if (__pt__queueIndexArray.length > 0) {//####[63]####
            taskinfo.setIsPipeline(true);//####[63]####
        }//####[63]####
        int[] __pt__taskIdIndexArray = new int[__pt__taskIdIndexList.size()];//####[63]####
        for (int __pt__i = 0; __pt__i < __pt__taskIdIndexArray.length; __pt__i++) {//####[63]####
            __pt__taskIdIndexArray[__pt__i] = __pt__taskIdIndexList.get(__pt__i);//####[63]####
        }//####[63]####
        taskinfo.setTaskIdArgIndexes(__pt__taskIdIndexArray);//####[63]####
        taskinfo.setParameters(numProcessors, states, task, s, closedStates);//####[63]####
        taskinfo.setMethod(__pt__getCosts_int_PriorityQueuePartialScheduleGrph_int_PartialScheduleGrph_HashSetString_method);//####[63]####
        taskinfo.setInstance(this);//####[63]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[63]####
    }//####[63]####
    public PriorityQueue<PartialScheduleGrph> __pt__getCosts(int numProcessors, PriorityQueue<PartialScheduleGrph> states, int task, PartialScheduleGrph s, HashSet<String> closedStates) {//####[63]####
        for (int pc = 1; pc <= numProcessors; pc++) //####[64]####
        {//####[64]####
            long start = System.currentTimeMillis();//####[65]####
            PartialScheduleGrph next = s.copy();//####[66]####
            next.addVertex(task);//####[67]####
            next.getVertexWeightProperty().setValue(task, input.getVertexWeightProperty().getValue(task));//####[68]####
            next.getVertexProcessorProperty().setValue(task, pc);//####[69]####
            int dependencyUpperBound = 0;//####[80]####
            for (int taskDp : input.getInNeighbours(task)) //####[81]####
            {//####[81]####
                int edgeTime = 0;//####[82]####
                if (next.getVertexProcessorProperty().getValue(taskDp) != pc) //####[83]####
                {//####[83]####
                    edgeTime = (int) input.getEdgeWeightProperty().getValue(input.getSomeEdgeConnecting(taskDp, task));//####[84]####
                }//####[86]####
                int totalTime = (int) (input.getVertexWeightProperty().getValue(taskDp) + next.getVertexStartProperty().getValue(taskDp) + edgeTime);//####[88]####
                if (totalTime > dependencyUpperBound) //####[91]####
                {//####[91]####
                    dependencyUpperBound = totalTime;//####[92]####
                }//####[93]####
            }//####[94]####
            int processorUpperBound = 0;//####[104]####
            for (int pcTask : next.getVertices()) //####[105]####
            {//####[105]####
                if (next.getVertexProcessorProperty().getValue(pcTask) == pc && pcTask != task) //####[106]####
                {//####[106]####
                    int totalTime = (int) (next.getVertexWeightProperty().getValue(pcTask) + next.getVertexStartProperty().getValue(pcTask));//####[107]####
                    if (totalTime > processorUpperBound) //####[109]####
                    {//####[109]####
                        processorUpperBound = totalTime;//####[110]####
                    }//####[111]####
                }//####[113]####
            }//####[114]####
            next.getVertexStartProperty().setValue(task, Math.max(processorUpperBound, dependencyUpperBound));//####[118]####
            if (!storedInClosedSet(next.getNormalizedCopy(numProcessors), closedStates)) //####[122]####
            {//####[122]####
                states.add(next);//####[123]####
            }//####[124]####
        }//####[126]####
        return states;//####[127]####
    }//####[128]####
//####[128]####
//####[129]####
    private boolean storedInClosedSet(PartialScheduleGrph g, Set<String> closedStates) {//####[129]####
        String serialized = new ScheduleDotWriter().createDotText(g, false);//####[131]####
        return closedStates.contains(serialized);//####[133]####
    }//####[134]####
}//####[134]####
