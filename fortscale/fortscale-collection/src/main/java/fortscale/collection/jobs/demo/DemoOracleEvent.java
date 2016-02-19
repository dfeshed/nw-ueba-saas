package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

import java.util.Random;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoOracleEvent extends DemoGenericEvent {

	private static final String DEFAULT_ACTION = "Login";
	private static final String SUCCESS_CODE = "0";

	private Computer srcMachine;
	private String[] dstMachines;
	private String dbObject;
	private String dbId;
	private String dbUsername;
	private String returnCode;
	private String actionType;

	public DemoOracleEvent() {}

	private DemoOracleEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
						   String[] dstMachines, String dbObject, String dbId, String dbUsername, String returnCode,
						   String actionType) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.dstMachines = dstMachines;
		this.dbObject = dbObject;
		this.dbId = dbId;
		this.dbUsername = dbUsername;
		this.returnCode = returnCode;
		this.actionType = actionType;
	}

	public static DemoOracleEvent createBaseLineConfiguration(User user, Computer srcMachine, String dstMachines[],
															  String dbObject, String dbId, String dbUsername) {
		return new DemoOracleEvent(user, DemoUtils.DEFAULT_SCORE, DemoUtils.EventFailReason.NONE, srcMachine,
				dstMachines, dbObject, dbId, dbUsername, SUCCESS_CODE, DEFAULT_ACTION);
	}

	public static DemoOracleEvent createAnomalyConfiguration(User user, Computer srcMachine, String[] dstMachines,
															 String dbObject, String dbId, String dbUsername,
															 String returnCode, String actionType, int score,
															 DemoUtils.EventFailReason reason) {
		return new DemoOracleEvent(user, score, reason, srcMachine, dstMachines, dbObject, dbId, dbUsername, returnCode,
				actionType);
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public String[] getDstMachines() {
		return dstMachines;
	}

	public String getDstMachine() {
		return dstMachines[0];
	}

	public String getDbObject() {
		return dbObject;
	}

	public String getDbId() {
		return dbId;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public String getActionType() {
		return actionType;
	}

	public void setSrcMachine(Computer srcMachine) {
		this.srcMachine = srcMachine;
	}

	public void setDstMachines(String[] dstMachines) {
		this.dstMachines = dstMachines;
	}

	public void setDbObject(String dbObject) {
		this.dbObject = dbObject;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case DEST: return dstMachines[0];
			case SOURCE: return srcMachine.getName();
			case USERNAME: return dbUsername;
			case OBJECT: return dbObject;
			case ACTION_TYPE: return actionType;
			default: return null;
		}
	}

	@Override
	protected DemoGenericEvent generateEvent() {
		Random random = new Random();
		String dstMachine = dstMachines[random.nextInt(dstMachines.length)];
		return new DemoOracleEvent(user, score, reason, srcMachine, new String[] { dstMachine }, dbObject, dbId,
				dbUsername, returnCode, actionType);
	}

}