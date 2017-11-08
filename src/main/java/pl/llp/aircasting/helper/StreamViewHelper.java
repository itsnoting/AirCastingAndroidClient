package pl.llp.aircasting.helper;

import android.view.View;
import android.widget.TextView;
import com.google.inject.Inject;
import pl.llp.aircasting.R;
import pl.llp.aircasting.model.Sensor;
import pl.llp.aircasting.model.Session;
import pl.llp.aircasting.model.CurrentSessionManager;

/**
 * Created with IntelliJ IDEA.
 * User: marcin
 * Date: 10/21/13
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class StreamViewHelper {
    @Inject CurrentSessionManager currentSessionManager;
    @Inject ResourceHelper resourceHelper;
    @Inject SessionDataFactory sessionData;

    private static List<Integer> positionsWithTitle = new ArrayList<Integer>();

    public void addPositionWithTitle(int position) {
        positionsWithTitle.add(position);
    }

    public void updateMeasurements(long sessionId, Sensor sensor, View view, int position) {
        int now = (int) currentSessionManager.getNow(sensor);
        TextView nowTextView = (TextView) view.findViewById(R.id.now);
        TextView sessionTitleView = (TextView) view.findViewById(R.id.session_title);

        if (positionsWithTitle.contains(position)) {
            setTitleView(sessionId, sessionTitleView);
        } else {
            sessionTitleView.setVisibility(View.GONE);
        }

        nowTextView.setBackgroundDrawable(resourceHelper.streamValueGrey);

        if (sessionData.isSessionRecording(sessionId)){
            setBackground(sensor, nowTextView, now);
        }

        if (sessionData.isSessionCurrent(sessionId)) {
            nowTextView.setText(String.valueOf(now));
        } else {
            nowTextView.setText(R.string.empty);
        }
    }

    private void setTitleView(long sessionId, TextView sessionTitleView) {
        Session session = sessionData.getSession(sessionId);

        sessionTitleView.setVisibility(View.VISIBLE);
        sessionTitleView.setCompoundDrawablesWithIntrinsicBounds(session.getDrawable(), 0, 0, 0);

        if (sessionData.isSessionRecording(sessionId)) {
            sessionTitleView.setText("Recording session");
        } else {
            sessionTitleView.setText(session.getTitle());
        }
    }

    private void setBackground(Sensor sensor, View view, double value) {
        view.setBackgroundDrawable(resourceHelper.getStreamValueBackground(sensor, value));
    }
}
