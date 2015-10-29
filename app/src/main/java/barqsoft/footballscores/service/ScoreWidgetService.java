package barqsoft.footballscores.service;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract.scores_table;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.scoresAdapter;


public class ScoreWidgetService extends RemoteViewsService {
    static final String TAG = ScoreWidgetService.class.getSimpleName();
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScoreRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ScoreRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private int mAppWidgetId;
        private Cursor mCursor;

        public ScoreRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            Date fragmentdate = new Date(System.currentTimeMillis());
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            String[] date = new String[1];
            date[0] = mformat.format(fragmentdate);

            mCursor = mContext.getContentResolver().
                                query(scores_table.buildScoreWithDate(),
                                        null, null, date, null);

            Log.v(TAG, "Cursor Count: " + mCursor.getCount());
            mCursor.moveToPosition(-1);
            while(mCursor.moveToNext()){
                String str = mCursor.getString(scoresAdapter.COL_HOME) + " v.s. "  +  mCursor.getString(scoresAdapter.COL_AWAY);
                Log.v(TAG, "Match: " + str);
            }
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            Log.v(TAG, "position " + i);
            mCursor.moveToPosition(i);
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.score_widget_item);

            rv.setTextViewText(R.id.home_name, mCursor.getString(scoresAdapter.COL_HOME));
            rv.setTextViewText(R.id.away_name, mCursor.getString(scoresAdapter.COL_AWAY));
            rv.setTextViewText(R.id.score_textview, Utilies.getScores(mCursor.getInt(scoresAdapter.COL_HOME_GOALS),
                                                                      mCursor.getInt(scoresAdapter.COL_AWAY_GOALS)));
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}

