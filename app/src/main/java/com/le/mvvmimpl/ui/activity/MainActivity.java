package com.le.mvvmimpl.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.le.mvvmimpl.R;
import com.le.mvvmimpl.db.ShoppingItem;
import com.le.mvvmimpl.ui.ShoppingListViewModel;
import com.le.mvvmimpl.util.UiUtils;

import rx.Subscription;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mShoppingListRv;
    private LinearLayoutManager mLinearLayoutManager;
    private ShoppingListViewModel mShoppingListViewModel;
    private ShoppingListAdapter mShoppingListAdapter;
    // Use CompositeSubscription if activity caters for multiple subscriptions
    private Subscription mSubscription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        mShoppingListRv = (RecyclerView) findViewById(R.id.shopping_list_rv);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mShoppingListRv.setLayoutManager(mLinearLayoutManager);
        mShoppingListViewModel = ViewModelProviders.of(this).get(ShoppingListViewModel.class);
        mSubscription = mShoppingListViewModel.shoppingListUpdates().subscribe(shoppingItems -> {
            updateShoppingList(shoppingItems);
        });
        createShoppingList();
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private void updateShoppingList(@NonNull List<ShoppingItem> shoppingItems) {
        mShoppingListAdapter.setShoppingList(shoppingItems);
        mShoppingListAdapter.notifyDataSetChanged();
    }

    private void createShoppingList() {
        mShoppingListAdapter = new ShoppingListAdapter(mShoppingListViewModel);
        mShoppingListRv.setAdapter(mShoppingListAdapter);
    }

    private class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.VH> {

        private List<ShoppingItem> mShoppingItems;
        private ShoppingListViewModel mViewModel;

        ShoppingListAdapter(@NonNull ShoppingListViewModel viewModel) {
            mViewModel = viewModel;
            mShoppingItems = viewModel.getShoppingItems();
        }

        public void setShoppingList(@NonNull List<ShoppingItem> shoppingItems) {
            mShoppingItems = shoppingItems;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.shopping_item_row, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int pos) {

            ShoppingItem item = mShoppingItems.get(pos);
            holder.mDescriptionTv.setText(item.getItemDescription());
            holder.mBrandTv.setText(item.getItemBrand());
            holder.mQuantityTv.setText(String.format("%d", item.getItemQuantity()));
            holder.mPriceTv.setText(String.format("%s %.02f", "£", item.getItemPrice()));

        }

        @Override
        public int getItemCount() {

            if (mShoppingItems == null) {
                return 0;
            } else {
                return mShoppingItems.size();
            }
        }

        class VH extends RecyclerView.ViewHolder {

            TextView mDescriptionTv, mBrandTv, mQuantityTv, mPriceTv;
            ImageView mBarcodeIv, mShareIv, mDeleteIv;
            ConstraintLayout mContentRootCl;
            CheckBox mCheckBox;
            CardView mRootCv;


            public VH(View v) {
                super(v);

                mRootCv = (CardView) v.findViewById(R.id.root_layout_cv);
                mContentRootCl = (ConstraintLayout) v.findViewById(R.id.shopping_item_row_cl);
                mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
                mDescriptionTv = (TextView) v.findViewById(R.id.description_tv);
                mBrandTv = (TextView) v.findViewById(R.id.brand_tv);
                mQuantityTv = (TextView) v.findViewById(R.id.quantity_tv);
                mPriceTv = (TextView) v.findViewById(R.id.price_tv);
                mBarcodeIv = (ImageView) v.findViewById(R.id.barcode_iv);
                mShareIv = (ImageView) v.findViewById(R.id.share_iv);
                mDeleteIv = (ImageView) v.findViewById(R.id.delete_iv);

                mBarcodeIv.setOnClickListener((view) -> {
                    int pos = getAdapterPosition();
                    UiUtils.showDialog(MainActivity.this, getString(R.string.barcode), mShoppingItems.get(pos).getItemBarcode());
                });

                mShareIv.setOnClickListener((view) -> {
                    UiUtils.showDialog(MainActivity.this, getString(R.string.barcode), getString(R.string.feature_not_implemented));
                });

                mDeleteIv.setOnClickListener((view) -> {
                    int pos = getAdapterPosition();
                    mViewModel.deleteShoppingItem(mShoppingItems.get(pos));
                });

                mContentRootCl.setOnClickListener((view) -> {
                    UiUtils.showDialog(MainActivity.this, null, getString(R.string.feature_not_implemented));
                });

            }
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
    }
}
