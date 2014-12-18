package com.vsis.drachenmobile.minigame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vsis.drachen.exception.DrachenBaseException;
import com.vsis.drachen.exception.InternalProcessException;
import com.vsis.drachen.exception.InvalidParameterException;
import com.vsis.drachen.exception.MissingParameterException;
import com.vsis.drachen.exception.RestrictionException;
import com.vsis.drachen.model.IMiniGame;
import com.vsis.drachen.model.ISensorSensitive;
import com.vsis.drachen.model.User;
import com.vsis.drachen.model.minigame.skirmish.Character;
import com.vsis.drachen.model.minigame.skirmish.MeeleAttack;
import com.vsis.drachen.model.minigame.skirmish.RandomSkillSelector;
import com.vsis.drachen.model.minigame.skirmish.Skill;
import com.vsis.drachen.model.minigame.skirmish.Skirmish;
import com.vsis.drachen.model.minigame.skirmish.Skirmish.ISkillListener;
import com.vsis.drachen.model.minigame.skirmish.Skirmish.PerformOrder;
import com.vsis.drachen.model.minigame.skirmish.Skirmish.SkirmishOutcome;
import com.vsis.drachen.sensor.SensorType;
import com.vsis.drachen.sensor.data.AccelarationSensorData;
import com.vsis.drachenmobile.DrachenApplication;
import com.vsis.drachenmobile.Main_Activity;
import com.vsis.drachenmobile.MyDataSet;
import com.vsis.drachenmobile.R;
import com.vsis.drachenmobile.Register_Activity;
import com.vsis.drachenmobile.helper.Helper;
import com.vsis.drachenmobile.helper.IActionDelegate;

public class Skirmish_Activity extends Activity {

	Skirmish skirmish;

	Skill userSkill;

	public Skirmish_Activity() {
		createDummy();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skirmish);

		DrachenApplication app = (DrachenApplication) getApplication();
		MyDataSet appdata = app.getAppData();
		IMiniGame minigame = appdata.getCurrentMinigame();
		if (minigame == null || !(minigame instanceof Skirmish))
			finish();

		skirmish = (Skirmish) minigame;
		skirmish.setSkillSensorSelectedListener(new ISkillListener() {

			@Override
			public void gotSkillForChar(final Character c, final Skill s) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						skillChosenBySensor(c, s);
					}
				});

			}
		});

		Button btnFight = (Button) findViewById(R.id.button1);
		Button btnRun = (Button) findViewById(R.id.button2);

		btnFight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performSelectSkillList();
			}
		});
		btnRun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performUserRun();
			}
		});

		updateBothCharacters();

		switch (skirmish.upcomingPhase()) {
		case CombatPhase:
			// go back to skillSelection...
			beginSkillSelectionPhase();
			break;
		case EndPhase:
			beginEndPhase();
			break;
		case InitialPhase:
			beginInitialPhase();
			break;
		case SkillSelectionPhase:
			beginSkillSelectionPhase();
			break;
		default:
			break;
		}
	}

	private void createDummy() {
		DrachenApplication app = (DrachenApplication) getApplication();
		MyDataSet appdata = app.getAppData();
		String username = appdata.getUser().getDisplayName();

		List<Skill> skills = new ArrayList<Skill>();
		skills.add(new MeeleAttack("Attack", "Kling klang", 10, 20, 15, 10));
		skills.add(new MeeleAttack("Crit", "Boooom", 30, 30, 20, 100));
		skills.add(new MeeleAttack("sorry", "meh.", 100, 100, 0, 0));
		skills.add(new MeeleAttack("Stomp", "Squeeeeeze", 200, 200, 0, 0, true));

		Character char_user = new Character(username, 200, skills, null);

		Character char2 = generateRandomOpponent();

		Skirmish skirmish = new Skirmish(char_user, char2);
		appdata.setCurrentMinigame(skirmish);
	}

	private Character generateRandomOpponent() {
		Random rnd = new Random();

		List<Skill> skills = new ArrayList<Skill>();
		// skills.add(new MeeleAttack("Attack", "Kling klang", 10, 20, 15, 10));
		// skills.add(new MeeleAttack("Other attack", "...", 15, 25, 30, 0));
		// skills.add(new MeeleAttack("sorry", "meh.", 100, 100, 0, 0));
		Character char2 = new Character("Not me", 200, skills,
				new RandomSkillSelector(skills));
		return char2;
	}

	/**
	 * Open an {@link AlertDialog} with a list of all non hidden skills of the
	 * user's character
	 */
	private void performSelectSkillList() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.select_a_skill);

		List<Skill> all = skirmish.getChar1().getSkills();
		final List<Skill> skills = new ArrayList<Skill>(all.size());
		for (Skill s : all) {
			if (!s.isHidden())
				skills.add(s);
		}
		String[] items = new String[skills.size()];
		for (int i = 0; i < skills.size(); i++) {
			items[i] = skills.get(i).getName();
		}
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which >= 0 && which < skills.size()) {
					Skill skill = skills.get(which);
					beginCombatPhase(skill);
				}
			}
		});

		builder.show();

	}

	private void performUserRun() {
		// skirmish.characterRun(true);
		//
		// beginEndPhase();
		;
		for (ISensorSensitive sr : skirmish.getSensorReceiver()) {
			if (sr.needsNewSensordata(SensorType.Accelaration))
				sr.receiveSensordata(SensorType.Accelaration,
						new AccelarationSensorData(System.currentTimeMillis(),
								System.nanoTime(), 0, 20, 0));
		}
	}

	/**
	 * Starting Phase: (maybe with introduction text)
	 */
	private void beginInitialPhase() {
		setButtonsEnabled(false);
		if (skirmish.checkEnd())
			beginEndPhase();
		else
			beginSkillSelectionPhase();
	}

	private void beginSkillSelectionPhase() {
		setButtonsEnabled(true);

	}

	private void beginCombatPhase(final Skill userSkill) {
		setButtonsEnabled(false);

		final int hpAnimationTime = 2500; // 1 second
		final Skill skill2 = skirmish.getChar2().getSkillSelector()
				.chooseSkill();

		final PerformOrder performOrder = skirmish.doTurn(userSkill, skill2);

		IActionDelegate cleanUp = new IActionDelegate() {

			@Override
			public void performAction() {
				TextView middleView = (TextView) findViewById(R.id.textView3);
				String text = "VS";
				middleView.setText(text);

				if (skirmish.checkEnd()) {
					beginEndPhase();
				} else {
					beginSkillSelectionPhase();
				}
			}
		};

		IActionDelegate action;
		switch (performOrder) {
		case OneOnly:
			action = chain_charUsesSkill(true, hpAnimationTime, userSkill,
					cleanUp);
			break;
		case OneToTwo:
			action = chain_charUsesSkill(
					true,
					hpAnimationTime,
					userSkill,
					chain_charUsesSkill(false, hpAnimationTime, skill2, cleanUp));
			break;
		case TwoOnly:
			action = chain_charUsesSkill(false, hpAnimationTime, skill2,
					cleanUp);
		case TwoToOne:
			action = chain_charUsesSkill(
					false,
					hpAnimationTime,
					skill2,
					chain_charUsesSkill(true, hpAnimationTime, userSkill,
							cleanUp));

			break;
		default:
			action = null;
			break;

		}
		if (action != null)
			action.performAction();
		// endPhase will be called by cleanUp (if needed)
	}

	private void beginEndPhase() {
		setButtonsEnabled(false);
		SkirmishOutcome result = skirmish.getOutCome();

		boolean win = false;
		String message;
		String title;
		switch (result) {
		case Char1Run:
			title = "Chicken out";
			message = "You're a little chicken.\nBut you escaped.";
			break;
		case Char1Win:
			win = true;
			title = "Yeah!";
			message = String.format("You crushed %s.", skirmish.getChar2()
					.getName());
			break;
		case Char2Run:
			win = true;
			title = "Roar!!!";
			message = String.format("You eat enough onien, so %s ran away.",
					skirmish.getChar2().getName());
			break;
		case Char2Win:
			title = "Loser";
			message = "Zero HP is your defeat.";
			break;
		case Duce:
			title = "Boring...";
			message = "No one wins.";
			break;
		case None:
		default:
			title = "???";
			message = "How did you got here?";
			break;

		}

		if (Build.VERSION.SDK_INT >= 17) {
			displayEndDialog(message, title);
		}

	}

	private void endMinigame() {
		// TODO: say Minigame that it is over (so events can be fired)
	}

	@TargetApi(17)
	private void displayEndDialog(String message, String title) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// finish this activity
				endMinigame();
				finish();
			}
		});
		builder.show();
	}

	private void setButtonsEnabled(boolean enabled) {
		Button btnFight = (Button) findViewById(R.id.button1);
		Button btnRun = (Button) findViewById(R.id.button2);

		btnFight.setEnabled(enabled);
		btnRun.setEnabled(enabled);
	}

	private void updateBothCharacters() {
		updateChar1(0, null);
		updateChar2(0, null);
	}

	private void updateChar1(int animationTime,
			final IActionDelegate animatenEnd) {
		TextView nameView = (TextView) findViewById(R.id.textView1);
		ProgressBar hpView = (ProgressBar) findViewById(R.id.progressBar1);

		displayCharacterValues(skirmish.getChar1(), nameView, hpView,
				animationTime, animatenEnd);
	}

	private void updateChar2(int animationTime,
			final IActionDelegate animatenEnd) {
		TextView nameView = (TextView) findViewById(R.id.textView2);
		ProgressBar hpView = (ProgressBar) findViewById(R.id.progressBar2);

		displayCharacterValues(skirmish.getChar2(), nameView, hpView,
				animationTime, animatenEnd);
	}

	private void displayCharacterValues(final Character c, TextView nameView,
			final ProgressBar hpView, int animationTime,
			final IActionDelegate animatenEnd) {
		// TODO: add image

		nameView.setText(c.getName());
		hpView.setMax(c.getMaxHP());

		if (animationTime <= 0)
			hpView.setProgress(c.getHP());
		else {
			ObjectAnimator animation = ObjectAnimator.ofInt(hpView, "progress",
					c.getHP());
			animation.setDuration(animationTime); // 0.5 second
			animation.setInterpolator(new DecelerateInterpolator());
			animation.start();
			animation.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					if (animatenEnd != null)
						animatenEnd.performAction();
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					// if the animation canceled make sure the the HP are set to
					// the correct value
					hpView.setProgress(c.getHP());
					if (animatenEnd != null)
						animatenEnd.performAction();
				}
			});
		}
	}

	/**
	 * display Text that character (see char1) uses the {@link Skill} skill
	 * 
	 * @param char1
	 *            true if character 1 ({@code skirmish.getChar1}) performs the
	 *            skill hitting character 2
	 * @param hpAnimationTime
	 * @param skill
	 * @param nextAction
	 *            action called after finished the animation
	 * @return delegate that performs the action
	 */
	private IActionDelegate chain_charUsesSkill(final boolean char1,
			final int hpAnimationTime, final Skill skill,
			final IActionDelegate nextAction) {
		return new IActionDelegate() {

			@Override
			public void performAction() {
				TextView middleView = (TextView) findViewById(R.id.textView3);

				Character c = char1 ? skirmish.getChar1() : skirmish.getChar2();

				String text = getString(R.string.char_uses_skill, c.getName(),
						skill.getName());
				middleView.setText(text);
				if (char1) // update the other char
					updateChar2(hpAnimationTime, nextAction);
				else
					updateChar1(hpAnimationTime, nextAction);
			}
		};
	}

	private void login() {
		String username = ((EditText) findViewById(R.id.editTextUsername))
				.getText().toString();
		String password = ((EditText) findViewById(R.id.editTextPassword))
				.getText().toString();

		LoginTask task = new LoginTask();
		task.execute(username, password);
	}

	private void signin() {
		Intent intent = new Intent(this, Register_Activity.class);

		startActivityForResult(intent, 1);
	}

	class LoginTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog ringProgressDialog;
		private DrachenBaseException _exception = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Context ctx = Skirmish_Activity.this;
			ringProgressDialog = ProgressDialog.show(ctx,
					ctx.getString(R.string.please_wait_),
					ctx.getString(R.string.logging_in), true);
			ringProgressDialog.setCancelable(true);
			ringProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					// actually could set running = false; right here, but I'll
					// stick to contract.
					boolean success = cancel(true);
				}
			});
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String username = params[0];
			String password = params[1];

			DrachenApplication app = (DrachenApplication) getApplication();
			MyDataSet client = app.getAppData();

			try {
				boolean success = client.login(username, password);
				return success;
			} catch (DrachenBaseException e) {
				_exception = e;
				return null;
			}

		}

		@Override
		protected void onCancelled(Boolean result) {
			super.onCancelled(result);
			// TODO evtl clean up
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result != null && result) {

				DrachenApplication app = (DrachenApplication) getApplication();
				User user = app.getAppData().getUser();

				app.startDrachenServices();

				Intent intent = new Intent(Skirmish_Activity.this,
						Main_Activity.class);

				startActivity(intent);
				ringProgressDialog.dismiss();
			} else {

				String message = getErrorString();

				ringProgressDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						Skirmish_Activity.this);
				builder.setTitle(R.string.login_failed);
				builder.setMessage(message);
				builder.show();
			}
		}

		private String getErrorString() {
			Context ctx = Skirmish_Activity.this;
			String message = ctx.getString(R.string.wrong_password);

			if (_exception != null) {
				if (_exception instanceof MissingParameterException) {
					MissingParameterException e = (MissingParameterException) _exception;
					message = ctx.getString(R.string.missing_parameter_s,
							e.getParameter());
				} else if (_exception instanceof InternalProcessException) {
					InternalProcessException e = (InternalProcessException) _exception;
					message = ctx.getString(R.string.internal_process_error,
							e.getMessage());

					// the following Exceptions doesn't occur while logging in
					// but at later requests
				} else if (_exception instanceof InvalidParameterException) {
					message = ctx.getString(R.string.grats_bug);
					message += "\n";
					message = Helper.getErrorStringForInvalidParameter(ctx,
							(InvalidParameterException) _exception);
				} else if (_exception instanceof RestrictionException) {
					// a strange exception:
					message = ctx.getString(R.string.grats_bug);
					message += "\n";
					message += ctx.getString(R.string.logged_in_no_access);
				}
			}
			return message;
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				String username = data.getStringExtra("username");
				String password = data.getStringExtra("password");

				((EditText) findViewById(R.id.editTextUsername))
						.setText(username);
				((EditText) findViewById(R.id.editTextPassword))
						.setText(password);
			}
			if (resultCode == RESULT_CANCELED) {

			}
		}
	}

	/**
	 * Run if the {@link Character} c has chosen the {@link Skill} by sensor
	 * event
	 * 
	 * @param c
	 *            the character who will use this skill
	 * @param s
	 *            the skill which will be used
	 */
	private void skillChosenBySensor(Character c, final Skill s) {
		if (s != null && c == skirmish.getChar1()) // user
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(
					Skirmish_Activity.this);
			String title = getString(R.string.use_skill) + ": " + s.getName();
			String message = s.getDescription();
			builder.setTitle(title);
			builder.setMessage(message);

			builder.setPositiveButton(R.string.use_skill,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							beginCombatPhase(s);
						}
					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});

			builder.show();
		}
	}
}
