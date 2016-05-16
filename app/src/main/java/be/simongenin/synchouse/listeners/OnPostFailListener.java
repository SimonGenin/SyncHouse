package be.simongenin.synchouse.listeners;

/**
 * @author Simon Genin
 *
 * Used when a post request fails. It then will be useful to update the UI.
 */
public interface OnPostFailListener {

    void onPostFail();

}
