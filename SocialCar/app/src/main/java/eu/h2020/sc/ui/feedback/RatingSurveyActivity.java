package eu.h2020.sc.ui.feedback;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.domain.Question;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.ui.feedback.task.UpdateFeedbackTask;
import eu.h2020.sc.utils.TextViewCustom;
import eu.h2020.sc.utils.WidgetHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */
//TODO:refactor
public class RatingSurveyActivity extends GeneralActivity {

    public static final String FEEDBACK_CREATED_KEY = "feedbackCreated";

    public final static String COMFORT_LEVEL_KEY = "COMFORT";
    public final static String ROUTE_KEY = "ROUTE";
    public final static String DRIVING_BEHAVIOUR_KEY = "DRIVING_BEHAVIOUR";
    public final static String DURATION_KEY = "DURATION";
    public final static String SATISFACTION_BEHAVIOUR_KEY = "SATISFACTION_BEHAVIOUR";
    public final static String PUNCTUATION_KEY = "PUNCTUATION";
    public final static String CARPOOLER_BEHAVIOUR_KEY = "CARPOOLER_BEHAVIOUR";
    private final int QUESTIONS_PER_PAGE = 3;

    private Menu menu;
    private int currentPage;
    private Feedback feedback;
    private Map<String, Question> questions;
    private List<TextViewCustom> questionsTitle;
    private View questions1View;
    private TextViewCustom question1InfoText;
    private View questions2View;
    private View questions3View;
    private Map<View, List<ImageView>> questionsUi;
    private View[] questionsViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_survey);

        this.feedback = (Feedback) this.getIntent().getExtras().getSerializable(FEEDBACK_CREATED_KEY);

        retrieveQuestions();
        initUI();
    }

    private void initUI() {
        this.currentPage = 1;
        initToolBar(false);
        setTitle();
        initBack();
        initQuestionUi();
        showQuestions();
        initEvents();
    }

    //TODO : REMOVE THIS METHOD AFTER TEST A!
    @Override
    public void initBack() {
        super.initBack();
    }

    //TODO : REMOVE THIS METHOD AFTER TEST A!
    @Override
    public void onBackPressed() {
        showNoCompleteFeedback();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        inflateMenuItem();
        return true;
    }

    private View.OnClickListener question1StarsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.box_add_rating_star_preview_1:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1)]).setRating(1);
                    fillStarEffect(questionsUi.get(questions1View), 1);
                    break;
                case R.id.box_add_rating_star_preview_2:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1)]).setRating(2);
                    fillStarEffect(questionsUi.get(questions1View), 2);
                    break;
                case R.id.box_add_rating_star_preview_3:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1)]).setRating(3);
                    fillStarEffect(questionsUi.get(questions1View), 3);
                    break;
                case R.id.box_add_rating_star_preview_4:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1)]).setRating(4);
                    fillStarEffect(questionsUi.get(questions1View), 4);
                    break;
                case R.id.box_add_rating_star_preview_5:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1)]).setRating(5);
                    fillStarEffect(questionsUi.get(questions1View), 5);
                    break;
            }
        }
    };
    private View.OnClickListener question2StarsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.box_add_rating_star_preview_1:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1) + 1]).setRating(1);
                    fillStarEffect(questionsUi.get(questions2View), 1);
                    break;
                case R.id.box_add_rating_star_preview_2:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1) + 1]).setRating(2);
                    fillStarEffect(questionsUi.get(questions2View), 2);
                    break;
                case R.id.box_add_rating_star_preview_3:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1) + 1]).setRating(3);
                    fillStarEffect(questionsUi.get(questions2View), 3);
                    break;
                case R.id.box_add_rating_star_preview_4:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1) + 1]).setRating(4);
                    fillStarEffect(questionsUi.get(questions2View), 4);
                    break;
                case R.id.box_add_rating_star_preview_5:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1) + 1]).setRating(5);
                    fillStarEffect(questionsUi.get(questions2View), 5);
                    break;
            }
        }
    };
    private View.OnClickListener question3StarsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.box_add_rating_star_preview_1:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1) + 2]).setRating(1);
                    fillStarEffect(questionsUi.get(questions3View), 1);
                    break;
                case R.id.box_add_rating_star_preview_2:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1) + 2]).setRating(2);
                    fillStarEffect(questionsUi.get(questions3View), 2);
                    break;
                case R.id.box_add_rating_star_preview_3:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1) + 2]).setRating(3);
                    fillStarEffect(questionsUi.get(questions3View), 3);
                    break;
                case R.id.box_add_rating_star_preview_4:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1) + 2]).setRating(4);
                    fillStarEffect(questionsUi.get(questions3View), 4);
                    break;
                case R.id.box_add_rating_star_preview_5:
                    ((Question) questions.values().toArray()[QUESTIONS_PER_PAGE * (currentPage - 1) + 2]).setRating(5);
                    fillStarEffect(questionsUi.get(questions3View), 5);
                    break;
            }
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_done:
                showProgressDialog();
                UpdateFeedbackTask feedbackTask = new UpdateFeedbackTask(this);
                feedbackTask.execute();
                return true;
            case R.id.action_go_next_step:
                this.currentPage++;
                setTitle();
                showQuestions();
                inflateMenuItem();
                return true;
            default:
                if (this.currentPage == 1)
                    this.onBackPressed();
                else {
                    this.currentPage--;
                    setTitle();
                    showQuestions();
                    inflateMenuItem();
                }
                return true;
        }
    }

    private void inflateMenuItem() {
        this.menu.clear();
        MenuItem item;
        if (this.currentPage == calculateNumberTotalPage()) {
            getMenuInflater().inflate(R.menu.menu_done, menu);
            item = menu.findItem(R.id.menu_done);
        } else {
            getMenuInflater().inflate(R.menu.menu_go_next, menu);
            item = menu.findItem(R.id.action_go_next_step);
        }
        item.setVisible(true);
    }

    private void retrieveQuestions() {
        this.questions = new HashMap<>();
        if (feedback.getRole().equalsIgnoreCase(Feedback.ROLE_DRIVER)) {
            this.questions.put(PUNCTUATION_KEY, new Question(String.format(getString(R.string.question_survey_punctuation), User.getFirstNameFromCompleteName(feedback.getReviewedName()))));
            this.questions.put(SATISFACTION_BEHAVIOUR_KEY, new Question(String.format(getString(R.string.question_survey_satisfaction), User.getFirstNameFromCompleteName(feedback.getReviewedName()))));
            this.questions.put(CARPOOLER_BEHAVIOUR_KEY, new Question(String.format(getString(R.string.question_survey_carpooler_behaviour), User.getFirstNameFromCompleteName(feedback.getReviewedName()))));
        } else {
            this.questions.put(COMFORT_LEVEL_KEY, new Question(getString(R.string.question_survey_comfort)));
            this.questions.put(ROUTE_KEY, new Question(String.format(getString(R.string.question_survey_route), User.getFirstNameFromCompleteName(feedback.getReviewedName()))));
            this.questions.put(DURATION_KEY, new Question(getString(R.string.question_survey_duration)));
            this.questions.put(DRIVING_BEHAVIOUR_KEY, new Question(String.format(getString(R.string.question_survey_driver_behaviour), User.getFirstNameFromCompleteName(feedback.getReviewedName()))));
            this.questions.put(SATISFACTION_BEHAVIOUR_KEY, new Question(String.format(getString(R.string.question_survey_satisfaction), User.getFirstNameFromCompleteName(feedback.getReviewedName()))));
            this.questions.put(PUNCTUATION_KEY, new Question(String.format(getString(R.string.question_survey_punctuation), User.getFirstNameFromCompleteName(feedback.getReviewedName()))));
        }
    }

    private void setTitle() {
        int numberTotalPage = calculateNumberTotalPage();
        if (numberTotalPage > 1) {
            setTitle(String.format(Locale.getDefault(), "%s %d/%d", getString(R.string.survey_title), currentPage, numberTotalPage));
        } else {
            setTitle(getString(R.string.survey_title));
        }
    }

    private int calculateNumberTotalPage() {
        float t = (float) this.questions.size() / (float) QUESTIONS_PER_PAGE;
        if (t > (int) t)
            return ((int) t) + 1;
        else
            return (int) t;
    }

    private void showQuestions() {

        this.question1InfoText.setVisibility(View.GONE);

        this.questions2View.setVisibility(View.GONE);
        this.questions3View.setVisibility(View.GONE);

        int j = 0;
        for (int i = QUESTIONS_PER_PAGE * (currentPage - 1); i < questions.values().size() && i < QUESTIONS_PER_PAGE * (currentPage); i++) {
            this.questionsViews[j].setVisibility(View.VISIBLE);
            this.questionsTitle.get(j).setText(((Question) questions.values().toArray()[i]).getText());
            fillStarEffect(questionsUi.get(this.questionsViews[j]), ((Question) questions.values().toArray()[i]).getRating());
            j++;
        }
    }

    private void initQuestionUi() {
        this.questions1View = findViewById(R.id.activity_survey_question_1_view);
        this.questions2View = findViewById(R.id.activity_survey_question_2_view);
        this.questions3View = findViewById(R.id.activity_survey_question_3_view);

        this.questionsViews = new View[3];
        this.questionsViews[0] = this.questions1View;
        this.questionsViews[1] = this.questions2View;
        this.questionsViews[2] = this.questions3View;

        this.question1InfoText = (TextViewCustom) this.questions1View.findViewById(R.id.box_add_rating_info_text);
        TextViewCustom question1Title = (TextViewCustom) this.questions1View.findViewById(R.id.box_add_rating_title_text);
        ImageView question1Star1 = (ImageView) this.questions1View.findViewById(R.id.box_add_rating_star_preview_1);
        ImageView question1Star2 = (ImageView) this.questions1View.findViewById(R.id.box_add_rating_star_preview_2);
        ImageView question1Star3 = (ImageView) this.questions1View.findViewById(R.id.box_add_rating_star_preview_3);
        ImageView question1Star4 = (ImageView) this.questions1View.findViewById(R.id.box_add_rating_star_preview_4);
        ImageView question1Star5 = (ImageView) this.questions1View.findViewById(R.id.box_add_rating_star_preview_5);
        ImageView[] question1Stars = {question1Star1, question1Star2, question1Star3, question1Star4, question1Star5};
        TextViewCustom question2Title = (TextViewCustom) this.questions2View.findViewById(R.id.box_add_rating_title_text);
        ImageView question2Star1 = (ImageView) this.questions2View.findViewById(R.id.box_add_rating_star_preview_1);
        ImageView question2Star2 = (ImageView) this.questions2View.findViewById(R.id.box_add_rating_star_preview_2);
        ImageView question2Star3 = (ImageView) this.questions2View.findViewById(R.id.box_add_rating_star_preview_3);
        ImageView question2Star4 = (ImageView) this.questions2View.findViewById(R.id.box_add_rating_star_preview_4);
        ImageView question2Star5 = (ImageView) this.questions2View.findViewById(R.id.box_add_rating_star_preview_5);
        ImageView[] question2Stars = {question2Star1, question2Star2, question2Star3, question2Star4, question2Star5};
        TextViewCustom question3Title = (TextViewCustom) this.questions3View.findViewById(R.id.box_add_rating_title_text);
        ImageView question3Star1 = (ImageView) this.questions3View.findViewById(R.id.box_add_rating_star_preview_1);
        ImageView question3Star2 = (ImageView) this.questions3View.findViewById(R.id.box_add_rating_star_preview_2);
        ImageView question3Star3 = (ImageView) this.questions3View.findViewById(R.id.box_add_rating_star_preview_3);
        ImageView question3Star4 = (ImageView) this.questions3View.findViewById(R.id.box_add_rating_star_preview_4);
        ImageView question3Star5 = (ImageView) this.questions3View.findViewById(R.id.box_add_rating_star_preview_5);
        ImageView[] question3Stars = {question3Star1, question3Star2, question3Star3, question3Star4, question3Star5};

        TextViewCustom[] questionsText = {question1Title, question2Title, question3Title};

        this.questionsUi = new HashMap<>();
        this.questionsUi.put(this.questions3View, Arrays.asList(question3Stars));
        this.questionsUi.put(this.questions2View, Arrays.asList(question2Stars));
        this.questionsUi.put(this.questions1View, Arrays.asList(question1Stars));

        this.questionsTitle = Arrays.asList(questionsText);
    }

    private void initEvents() {
        for (ImageView starImageView : this.questionsUi.get(this.questions1View)) {
            starImageView.setOnClickListener(question1StarsListener);
        }
        for (ImageView starImageView : this.questionsUi.get(this.questions2View)) {
            starImageView.setOnClickListener(question2StarsListener);
        }
        for (ImageView starImageView : this.questionsUi.get(this.questions3View)) {
            starImageView.setOnClickListener(question3StarsListener);
        }
    }

    private void fillStarEffect(List<ImageView> imageViewsStars, Integer starsToFill) {
        if (starsToFill == null)
            starsToFill = 0;
        for (int i = 0; i < starsToFill; i++) {
            imageViewsStars.get(i).setImageResource(R.drawable.ic_star_add_rating_filled);
        }
        for (int i = 4; i >= starsToFill; i--) {
            imageViewsStars.get(i).setImageResource(R.drawable.ic_star_add_rating_border);
        }
    }

    public Map<String, Question> getQuestions() {
        return this.questions;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    //TODO : REMOVE THIS METHOD AFTER TEST A!
    public void showNoCompleteFeedback() {
        WidgetHelper.showToast(this, getString(R.string.feedback_not_complete_error));
    }
}
