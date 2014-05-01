package Services.AutoCompleteSerivce;

import Workers.GetAllSpecsListWorker;

import java.util.List;

/**
 * Created by aaronfleshner on 4/27/14.
 */
public class GearsService implements CompletionService<String> {

    /** Our name data. */
    private List<String> data;

    /**
     * Create a new <code>NameService</code> and populate it.
     */
    public GearsService() {
        getSpecs();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (String o : data) {
            b.append(o).append("\n");
        }
        return b.toString();
    }

    /** {@inheritDoc} */
    public String autoComplete(String startsWith) {
        startsWith = startsWith.toLowerCase();

        // Naive implementation, but good enough for the sample
        String hit = null;
        for (String o : data) {
            if (o.toLowerCase().startsWith(startsWith)) {
                // CompletionService contract states that we only
                // should return completion for unique hits.
                if (hit == null) {
                    hit = o;
                } else {
                    hit = null;
                    break;
                }
            }
        }
        return hit;
    }

    private void getSpecs() {
        //Get availableGears and reload
        GetAllSpecsListWorker worker = new GetAllSpecsListWorker() {
            @Override
            protected void done() {
                super.done();
                data = this.specNames;
            }
        };
        worker.execute();
    }

}
