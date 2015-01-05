package com.vsis.drachen.model.minigame.skirmish;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.vsis.drachen.model.IMiniGame;
import com.vsis.drachen.model.ISensorSensitive;
import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.AccelarationSensorData;
import com.vsis.drachen.sensor.data.ISensorData;

/**
 * turn-based battle
 * 
 */
public class Skirmish implements IMiniGame {

	public interface ISkillListener {
		public void gotSkillForChar(Character c, Skill s);
	}

	public static enum PerformOrder {
		OneToTwo, TwoToOne, OneOnly, TwoOnly
	}

	public static enum Phase {
		InitialPhase, SkillSelectionPhase, CombatPhase, EndPhase
	}

	public static enum SkirmishOutcome {
		None, Char1Win, Char2Win, Duce, Char1Run, Char2Run
	}

	/**
	 * this can be the user
	 */
	private Character char1;
	/**
	 * this can be the opponent
	 */
	private Character char2;

	/**
	 * number of the current turn (starting with 1)
	 */
	private int turn;

	Random rnd = new Random();
	private ISkillListener skillListener;
	private Phase phase = Phase.InitialPhase;
	private int charRan = 0;

	private List<ISensorSensitive> sensorReceivers;

	public Skirmish(Character char1, Character char2) {
		turn = 1;
		this.char1 = char1;
		this.char2 = char2;

		sensorReceivers = new ArrayList<ISensorSensitive>();
		sensorReceivers.add(new ISensorSensitive() {
			private final EnumSet<SensorType> sensorTypes = EnumSet
					.of(SensorType.Accelaration);
			// private int lastTurn = 0;
			private long lastNano = Long.MIN_VALUE;

			@Override
			public Set<SensorType> requiredSensors() {
				return sensorTypes;
			}

			@Override
			public synchronized boolean receiveSensordata(SensorType type,
					ISensorData data) {
				assert (type == SensorType.Position);

				if (!needsNewSensordata(type))
					return false;
				// wait 3 seconds until trigger event again
				if (lastNano == Long.MIN_VALUE) {
					lastNano = data.getNanoTime();
					return false;
				}
				if (Math.abs(data.getNanoTime() / 1000000 - lastNano / 1000000) < 3000)
					return false;
				// if (isFulfilled() && onlyOnce)
				// return false;

				AccelarationSensorData accelData = (AccelarationSensorData) data;

				double ax = accelData.getAx();
				double ay = accelData.getAy();
				double az = accelData.getAz();

				double sum = ax * ax + ay * ay + az * az;
				boolean success = sum >= 9.81 * 9.81 * 4;

				if (success) {
					lastNano = data.getNanoTime();
					// lastTurn = turn;

					// for testing take the first hidden skill
					Skill skill = null;
					for (Skill s : Skirmish.this.char1.getSkills()) {
						if (s.isHidden()) {
							skill = s;
							break;
						}
					}
					if (skill != null)
						callSkillListener(Skirmish.this.char1, skill);
				}
				return false;
			}

			@Override
			public boolean needsNewSensordata(SensorType type) {
				return phase == Phase.SkillSelectionPhase
						&& sensorTypes.contains(type);
			}
		});
	}

	public int getTurn() {
		return turn;
	}

	@Override
	public void begin() {
		// TODO Auto-generated method stub
		phase = Phase.SkillSelectionPhase;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ISensorSensitive> getSensorReceiver() {
		// TODO Auto-generated method stub

		// input via:
		// screen buttons, its done separately
		// voice input => trigger event notifying the Activity display
		// Acceleration ?? => Skill/and let it select it
		//

		return sensorReceivers;
	}

	public void setSkillSensorSelectedListener(ISkillListener listener) {
		skillListener = listener;
	}

	private void callSkillListener(Character c, Skill s) {
		if (skillListener != null)
			skillListener.gotSkillForChar(c, s);
	}

	/**
	 * 
	 * @param skill1
	 * @param skill2
	 */
	public PerformOrder doTurn(Skill skill1, Skill skill2) {
		phase = Phase.CombatPhase;
		Damage dmg1 = skill1.calculateDamage();
		Damage dmg2 = skill2.calculateDamage();

		// if true skill1 will be performed first (before skill2)
		// speed * 2 -> even number, + [-1,1] makes the other speed odd
		// so there is never the chance that the speeds are equals
		boolean first1 = (skill1.getSpeed() * 2) > (skill2.getSpeed() * 2 + (rnd
				.nextInt(2) * 2 - 1));
		PerformOrder result = first1 ? PerformOrder.OneToTwo
				: PerformOrder.TwoToOne;
		if (first1) {
			char2.receiveDamage(dmg1);
			if (!checkEnd())
				char1.receiveDamage(dmg2);
			else
				result = PerformOrder.OneOnly;
		} else {
			char1.receiveDamage(dmg2);
			if (!checkEnd())
				char2.receiveDamage(dmg1);
			else
				result = PerformOrder.TwoOnly;
		}
		turn++;
		if (checkEnd())
			phase = Phase.EndPhase;
		else
			phase = Phase.SkillSelectionPhase;
		return result;
	}

	/**
	 * checks if the skirmish ended (or should end)
	 * 
	 * @return true if either the HP of one char is zero or a character ran away
	 */
	public boolean checkEnd() {
		return charRan != 0 || char1.getHP() <= 0 || char2.getHP() <= 0;
	}

	public Character getChar1() {
		return char1;
	}

	public Character getChar2() {
		return char2;
	}

	/**
	 * the next desired Phase (defines the function that will be called)
	 * 
	 * @return
	 */
	public Phase upcomingPhase() {
		return phase;
	}

	public SkirmishOutcome getOutCome() {
		if (checkEnd())
			if (charRan == 1)
				return SkirmishOutcome.Char1Run;
			else if (charRan == 2)
				return SkirmishOutcome.Char2Run;
			else if (char1.getHP() <= 0 && char2.getHP() <= 0)
				return SkirmishOutcome.Duce;
			else if (char1.getHP() == 0)
				return SkirmishOutcome.Char2Win;
			else
				return SkirmishOutcome.Char1Win;

		return SkirmishOutcome.None;
	}

	/**
	 * declares that the character ran away
	 * 
	 * @param char1
	 *            true if char1 ran, false for char2
	 */
	public void characterRun(boolean char1) {
		charRan = char1 ? 1 : 2;
	}
}
