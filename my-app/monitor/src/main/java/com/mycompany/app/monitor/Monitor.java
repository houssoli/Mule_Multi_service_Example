package com.mycompany.app.monitor;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.openspaces.admin.Admin;
import org.openspaces.admin.AdminFactory;
import org.openspaces.admin.pu.ProcessingUnit;
import org.openspaces.core.GigaSpace;
import org.openspaces.core.context.GigaSpaceContext;
import com.mycompany.app.common.Data;

/**
 * AN SLA Monitor Service
 */
public class Monitor extends TimerTask {

	public Monitor() {
	}

	Data template;

	String monitoredPU;

	int highLimit;

	int lowLimit;

	String locators;

	Admin admin = null;

	ProcessingUnit pu = null;

	boolean increase = false;

	boolean decrease = false;

	static boolean inSleep = false;

	int state;

	int maxInstances;

	int minInstances;

	int sleeptimeBeforeNextCheckAfterScaleup;

	int sleeptimeBeforeNextCheckAfterScaledown;

	@GigaSpaceContext
	GigaSpace gigaspace;

	public void run() {
		if (inSleep)
			return;
		monitor();
	}

	public void monitor() {
		if (template == null) {
			template = new Data();
			template.setState(state);
		}
		System.out.println("------> Monitoring:" + monitoredPU);
		if (gigaspace == null) {
			System.out.println("No GigaSpace Instance");
			return;
		}

		if (admin == null) {
			admin = new AdminFactory().createAdmin();
			if (admin!= null)
				System.out.println("------> created Admin OK!");
		}
		int retyCount=0;
		while (pu == null)
		{
			pu = admin.getProcessingUnits().waitFor(monitoredPU,10000,TimeUnit.MILLISECONDS);
			if  (pu == null) {
				retyCount++; if (retyCount ==5) break;
				System.out.println(monitoredPU +" Processing Unit Can't be found!");				
			}
			else
			{
				System.out.println(monitoredPU + " PU Found");
				break;
			}
		}
		
		int puInstanceCount = pu.getNumberOfInstances();
		int count = gigaspace.count(template);
		System.out.println("There are " + count + " Data Objects within the space with State="+ state);
		System.out.println(monitoredPU + " PU Has " + puInstanceCount+ " instances");
		System.out.println("Objects Per Service Instance:" + count / puInstanceCount);
		
		// Here we determin if there is a need to scale up or down
		// We check amount of objects in space with state=1
		// if average amount is above the max limit - we increase amount of Service PU instances
		// if average amount is below the min limit - we decrease amount of Service PU instances
		increase = ((count / puInstanceCount) > highLimit);
		decrease = (count / puInstanceCount < lowLimit);

		if (increase || decrease) {
			System.out.println(monitoredPU + " PU Has " + puInstanceCount+ " instances");
			if (increase) {
				if (puInstanceCount == maxInstances) {
					System.out.println("Can't increase amount of "
							+ monitoredPU + " instances. There can be max of "
							+ maxInstances + " instnaces");
				} else {
					System.out.println("increasing amount of " + monitoredPU
							+ " instances");
					pu.incrementInstance();

					sleep(sleeptimeBeforeNextCheckAfterScaledown);
				}
			}

			if (decrease) {
				if (puInstanceCount > minInstances) {
					System.out.println("decrementing amount of " + monitoredPU
							+ " instances");
					pu.decrementInstance();
					sleep(sleeptimeBeforeNextCheckAfterScaleup);
				} else {
					System.out.println("Can't decrement " + monitoredPU
							+ " instances. There must be at least "
							+ minInstances+ " instance!");
				}
			}

			sleep(10000);
			System.out.println(monitoredPU + " PU Has "+ pu.getNumberOfInstances() + " instances");
		} 
	}

	public int getHighLimit() {
		return highLimit;
	}

	public void setHighLimit(int highLimit) {
		this.highLimit = highLimit;
	}

	public int getLowLimit() {
		return lowLimit;
	}

	public void setLowLimit(int lowLimit) {
		this.lowLimit = lowLimit;
	}

	public String getMonitoredPU() {
		return monitoredPU;
	}

	public void setMonitoredPU(String monitoredPU) {
		this.monitoredPU = monitoredPU;
	}

	public String getLocators() {
		return locators;
	}

	public void setLocators(String locators) {
		this.locators = locators;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getMaxInstances() {
		return maxInstances;
	}

	public void setMaxInstances(int maxInstances) {
		this.maxInstances= maxInstances;
	}

	public int getMinInstances() {
		return minInstances;
	}

	public void setMinInstances(int minInstances) {
		this.minInstances= minInstances;
	}

	void sleep(int time_ms) {
		inSleep = true;
		try {
			Thread.sleep(time_ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inSleep = false;
	}

	public int getSleeptimeBeforeNextCheckAfterScaledown() {
		return sleeptimeBeforeNextCheckAfterScaledown;
	}

	public void setSleeptimeBeforeNextCheckAfterScaledown(
			int sleeptimeBeforeNextCheckAfterScaledown) {
		this.sleeptimeBeforeNextCheckAfterScaledown = sleeptimeBeforeNextCheckAfterScaledown;
	}

	public int getSleeptimeBeforeNextCheckAfterScaleup() {
		return sleeptimeBeforeNextCheckAfterScaleup;
	}

	public void setSleeptimeBeforeNextCheckAfterScaleup(
			int sleeptimeBeforeNextCheckAfterScaleup) {
		this.sleeptimeBeforeNextCheckAfterScaleup = sleeptimeBeforeNextCheckAfterScaleup;
	}
	
}
