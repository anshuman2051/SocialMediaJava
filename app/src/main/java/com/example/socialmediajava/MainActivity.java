package com.example.socialmediajava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.socialmediajava.Fragment.PowFragment;
import com.example.socialmediajava.databinding.ActivityMainBinding;
import com.example.socialmediajava.databinding.ContentMainBinding;
import com.example.socialmediajava.managers.BlockchainManager;
import com.example.socialmediajava.managers.SharedPreferencesManager;
import com.example.socialmediajava.utils.CipherUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ContentMainBinding viewBindingContent;
    private ProgressDialog progressDialog;
    private SharedPreferencesManager prefs;
    private BlockchainManager blockChain;
    private boolean isEncryptionActivated, isDarkActivated;

    private static final String TAG_POW_DIALOG = "proof_of_work_dialog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("entry msg", " on Creates msg");
        prefs = new SharedPreferencesManager(this);
        isDarkActivated = prefs.isDarkTheme();
        PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        boolean isPowerSaverMode = false;

        if( powerManager != null){
            isPowerSaverMode = powerManager.isPowerSaveMode();
        }
        if( isPowerSaverMode){
            isPowerSaverMode = powerManager.isPowerSaveMode();
        }
        else{
            if( isDarkActivated){
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                );
            }
            else{
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );
            }
        }

        super.onCreate(savedInstanceState);


        ActivityMainBinding viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        viewBindingContent = ContentMainBinding.bind(viewBinding.contentMain.getRoot());
        setContentView(R.layout.activity_main);
        isEncryptionActivated = prefs.getEncryptionStatus();
        viewBindingContent.recyclerContent.setHasFixedSize(true);
        viewBindingContent.recyclerContent.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        Log.d("content " , ""+viewBindingContent.recyclerContent);
        showProgressDialog(getResources().getString(R.string.text_creating_block_chain));

        blockChain = new BlockchainManager(prefs.getPowValue(), this );
        viewBindingContent.recyclerContent.setAdapter(null);
        cancelProgressDialog(progressDialog);

//        new Thread( ()-> runOnUiThread( ()-> {
//            blockChain = new BlockchainManager(prefs.getPowValue(), this );
//            viewBindingContent.recyclerContent.setAdapter(blockChain.adapter);
//            cancelProgressDialog(progressDialog);
//        })).start();
        viewBindingContent.btnSendData.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startBlockChain(){
        showProgressDialog(getResources().getString(R.string.text_mining_blocks));
        runOnUiThread( ()->{
            if( blockChain != null && viewBindingContent.editMessage.getText() != null && viewBindingContent.recyclerContent.getAdapter() != null){
                String message = viewBindingContent.editMessage.getText().toString();

                if( message.isEmpty()){
                    if( !isEncryptionActivated){
                        blockChain.addBlock(blockChain.newBlock(message));
                    }
                    else{
                        try{
                            blockChain.addBlock(blockChain.newBlock(CipherUtils.encryptIt(message).trim()));
                        }
                        catch ( Exception e){
                            e.printStackTrace();
                            Toast.makeText(this, "something wrong happened", Toast.LENGTH_LONG).show();
                        }
                    }
                    viewBindingContent.recyclerContent.scrollToPosition(blockChain.adapter.getItemCount()-1);

                    if( blockChain.isBlockChainValid()){
                        viewBindingContent.recyclerContent.getAdapter().notifyDataSetChanged();
                        viewBindingContent.editMessage.setText("");
                    }
                    else {
                        Toast.makeText(this, "Blockchain corrupted", Toast.LENGTH_LONG).show();
                    }

                }
                else {
                    Toast.makeText(this, "Empty data", Toast.LENGTH_LONG).show();
                }
                cancelProgressDialog(progressDialog);
            }
            else {
                Toast.makeText(this, "Something Wrong Happened", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_send_data){
            Log.d("view", ""+view.getId());
            startBlockChain();
        }
    }

    private void showProgressDialog(@NonNull String loadingMessage){
        progressDialog = new ProgressDialog( MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    private  void cancelProgressDialog( @NonNull ProgressDialog progressDialog){
        if( progressDialog != null){
            progressDialog.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkEncrypt = menu.findItem(R.id.action_encrypt);
        checkEncrypt.setChecked(isEncryptionActivated);
        MenuItem checkTheme = menu.findItem(R.id.action_dark);
        checkTheme.setChecked(isDarkActivated);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ( item.getItemId()){
            case R.id.action_pow:
                PowFragment powFragment = PowFragment.newInstance();
                powFragment.show( this.getSupportFragmentManager(), TAG_POW_DIALOG);
                break;

            case R.id.action_encrypt:
                isEncryptionActivated = !item.isChecked();
                if( item.isChecked()){
                    Toast.makeText(this, "Message encryption is on", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Message Encryption off", Toast.LENGTH_SHORT).show();
                }
                prefs.setEncryptionStatus(isEncryptionActivated);
                return true;

            case R.id.action_dark :
                isDarkActivated = !item.isChecked();
                item.setChecked(isDarkActivated);
                prefs.setDarkTheme(isDarkActivated);
                Intent intent = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
                startActivity(intent);
                finish();
                return true;

            case R.id.action_exit:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);

    }
}