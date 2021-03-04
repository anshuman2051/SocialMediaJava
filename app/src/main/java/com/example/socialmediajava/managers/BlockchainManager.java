package com.example.socialmediajava.managers;

import android.content.Context;

import com.example.socialmediajava.Models.BlockModel;
import com.example.socialmediajava.adapter.BlockAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BlockchainManager {
    private  int difficulty;
    private List<BlockModel> blocks;
    public final BlockAdapter adapter;

    public BlockchainManager(int difficulty, @NonNull Context context) {
        this.difficulty = difficulty;
        blocks = new ArrayList<>();
        BlockModel block = new BlockModel( 0, System.currentTimeMillis(), null, "Genesis Block");
        block.mineBlock(difficulty);
        blocks.add(block);

        adapter = new BlockAdapter(blocks, context);
    }

    public BlockModel newBlock(String data){
        BlockModel latestBlock = lastestBlock();
        return new BlockModel( latestBlock.getIndex()+1, System.currentTimeMillis(), latestBlock.getHash(), data);
    }

    private BlockModel lastestBlock() {
        return blocks.get(blocks.size()-1);
    }

    public void addBlock(BlockModel block){
        if( block != null){
            block.mineBlock(difficulty);
            blocks.add(block);
        }
    }

    private  boolean isFirstBlockValid(){
        BlockModel firstBlock = blocks.get(0);
        if( firstBlock.getIndex() != 0){
            return  false;
        }
        if( firstBlock.getPreviousHash() != null){
            return  false;
        }
        return  firstBlock.getHash() != null && BlockModel.calculateHash_detail(firstBlock).equals( firstBlock.getHash());
    }

    private boolean isValidNewBlock(@Nullable BlockModel newBlock, @Nullable BlockModel previousBlock){
        if( newBlock != null && previousBlock != null){
            if( previousBlock.getIndex()+1 != newBlock.getIndex()){
                return false;
            }
            if( newBlock.getPreviousHash() == null || !newBlock.getPreviousHash().equals(newBlock.getData())){
                return  false;
            }
            return  newBlock.getHash() != null &&
                    BlockModel.calculateHash_detail(newBlock).equals(newBlock.getHash());
        }
        return false;
    }

    public boolean isBlockChainValid(){
        if( !isFirstBlockValid()){
            return  false;
        }
        for ( int i = 1; i < blocks.size(); i++){
            BlockModel currentBlock = blocks.get(i);
            BlockModel previousBlock = blocks.get(i-1);
            if( !isValidNewBlock(currentBlock, previousBlock))
                return false;
        }
        return  true;
    }


}
