package org.pentaho.dataflow.tck;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.pentaho.dataflow.DataRow;
import org.pentaho.dataflow.Step;
import org.pentaho.dataflow.StringDataRow;
import org.pentaho.dataflow.Transform;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by nbaker on 11/7/16.
 */
public abstract class AbstractTransformTest {
    List<String> words;

    @Rule
    public ContiPerfRule i = new ContiPerfRule();

    @Before
    public void setup() throws Exception {
        URL resource = getClass().getResource("/MobyDick.txt");
        words = Files.lines(Paths.get(resource.toURI())).flatMap(row -> Stream.of(row.split(" "))).collect(Collectors.toList());
    }

    protected abstract Transform createTransform();

    /**
     * This transform simply grabs another random word from Moby Dick
     * @param dataRow
     * @return
     */
    public DataRow stepFunc( DataRow dataRow ){
        Object[] newData = new Object[ dataRow.getSize()];
        for (int i = 0; i < dataRow.getSize(); i++) {
            newData[i] = words.get(  (int) (Math.random() * words.size() ) );
        }
        return StringDataRow.of(newData);
    }

    @Test
    @PerfTest(invocations = 100, threads = 20)
    public void testTransform() throws Exception {

       Transform transform = createTransform();
        int numOfSteps = 30;

        for (int j = 0; j < numOfSteps; j++) {
            transform.next( this::stepFunc );
        }

        TableModel model = generateTableModel(20/*cols*/, 5_000/*rows*/);

        transform.prepare();

        for(int i = 0; i < model.getRowCount(); i++) {
            Object[] data = new Object[model.getColumnCount()];
            Class<?>[] types = new Class[model.getColumnCount()];

            for (int j = 0; j < data.length; j++) {
                data[j] = model.getValueAt( i, j );
                types[j] = model.getColumnClass( j );
            }
            transform.onNext( new DataRow( data, types ) );
        }
        transform.shutdown();

    }


    TableModel generateTableModel(int columns, int rows ){
        DefaultTableModel defaultTableModel = new DefaultTableModel(rows, columns);
        for (int row = 0; row < rows; row++) {
             for( int col = 0; col < columns; col++ ){
                 defaultTableModel.setValueAt(words.get(  (int) (Math.random() * words.size() ) ), row, col);
             }
        }
        return defaultTableModel;
    }
}
