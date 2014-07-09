package org.almiso.collageapp.android.preview.user.dependence;

import org.almiso.collageapp.android.base.CollageApplication;
import org.almiso.collageapp.android.core.model.InstaUserDependence;
import org.almiso.collageapp.android.preview.media.BaseTask;
import org.almiso.collageapp.android.preview.queue.QueueWorker;

/**
 * Created by almiso on 22.06.2014.
 */
public class InstaUserDependenceLoader extends BaseUserLoader<BaseTask> {
    protected static final String TAG = "InstaUserDependenceLoader";


    private final Object locker = new Object();


    public InstaUserDependenceLoader(CollageApplication application) {
        super("user_dependence", application);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected QueueWorker<BaseTask>[] createWorkers() {
        return new QueueWorker[]{new MapWorker()};
    }


    public void requestSearchUser(InstaUserDependence result, UserDependenceReceiver receiver) {
        requestTask(new SearchInstaUserDependenceTask(result), receiver);
    }


    private class MapWorker extends QueueWorker<BaseTask> {

        private MapWorker() {
            super(processor);
        }

        @Override
        protected boolean needRepeatOnError() {
            return true;
        }

        protected void processWeb(SearchInstaUserDependenceTask task) throws Exception {
            InstaUserDependence dependence = application.getApi().getUserDependence(task.getResult().getUserId());
            synchronized (locker) {
                onDependenceLoaded(dependence, task);
            }
        }

        @Override
        protected boolean processTask(BaseTask task) throws Exception {

            if (task instanceof SearchInstaUserDependenceTask) {
                processWeb((SearchInstaUserDependenceTask) task);
            }
            return true;
        }

        @Override
        public boolean isAccepted(BaseTask task) {
            return task instanceof SearchInstaUserDependenceTask;
        }
    }
}
