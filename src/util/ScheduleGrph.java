package util;

import grph.in_memory.InMemoryGrph;
import grph.properties.NumericalProperty;;

public class ScheduleGrph extends InMemoryGrph{


	private NumericalProperty verticesWeight;
	private NumericalProperty verticesStart;
	private NumericalProperty verticesProcessor;
	
	public void setVertexStartProperty(NumericalProperty vertStarts)
	{
		this.verticesStart = vertStarts;
	}
	
	public void setVertexWeightProperty(NumericalProperty vertWeights)
	{
		this.verticesStart = vertWeights;
	}
	
	public void setVertexProcessorProperty(NumericalProperty vertProcs)
	{
		this.verticesStart = vertProcs;
	}
	
	
	public NumericalProperty getVertexStartProperty()
	{
		return verticesStart;
	}
	
	public NumericalProperty getVertexWeightProperty()
	{
		return verticesWeight;
	}
	
	public NumericalProperty getVertexProcessorProperty()
	{
		return verticesProcessor;
	}
	
	
	
}
